package com.ded.necronmod.radio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

import com.ded.necronmod.Config;
import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.radio.data.RadioStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Клиентский «диспетчер радио». Знает, ЧТО должно звучать, и приводит реальные SoundInstance в соответствие.
 *
 * Три вида источников:
 *  - BLOCK:  радио-блок в мире (update / remove). Слышно всем вокруг, моно, затухание.
 *  - PLAYER: переносное радио ДРУГОГО игрока в режиме «динамик» (updatePlayer / removePlayer). Звук следует за
 *            игроком, дальность меньше, чем у блока.
 *  - OWN:    собственное переносное радио (updateOwnRadio): и «наушники», и «динамик» владелец слышит одинаково,
 *            стерео прямо в ушах. Режим «динамик» нужен только для того, чтобы услышали другие (это решает сервер
 *            и рассылает их клиентам, которые вызывают updatePlayer).
 *
 * Правила (на каждом клиенте независимо):
 *  - Один сетевой поток на клиент. Собственное радио, если оно включено, имеет приоритет над всем остальным;
 *    иначе играет ближайший из блоков и чужих «динамиков».
 *  - Станция подключается через DEBOUNCE_TICKS после выбора: пока крутят ручку, слышен шум, и мы не долбим
 *    чужие серверы десятками соединений.
 *  - Шум плавно уступает место станции и возвращается, если поток оборвался или не подключился.
 *  - Если интернет-радио выключено в клиентском конфиге, сети нет вовсе, а вместо станций играет мелодия-затычка.
 * Все методы вызываются из главного клиентского потока.
 */
@EventBusSubscriber(modid = DedNecronMod.MODID, value = Dist.CLIENT)
public final class RadioSoundManager {
    private static final double BLOCK_START_SQ = 22.0 * 22.0;  // начинаем слушать блок
    private static final double BLOCK_STOP_SQ = 28.0 * 28.0;   // перестаём (запас против дрожания на границе)
    private static final double PLAYER_START_SQ = 14.0 * 14.0; // чужой динамик слышно ближе (sounds.json: 16 блоков)
    private static final double PLAYER_STOP_SQ = 18.0 * 18.0;
    private static final int DEBOUNCE_TICKS = 8;               // 0,4 с
    private static final int RETRY_TICKS = 20 * 60;            // повтор после неудачи
    private static final int REFUSED_RETRY_TICKS = 20 * 10;    // повтор, если движок не принял звук
    private static final int ACCEPT_TICKS = 20;                // за это время движок обязан начать тикать звук
    private static final int PLAYER_MISSING_TICKS = 200;       // 10 с без игрока в мире: забываем его состояние

    private enum Kind { BLOCK, PLAYER, OWN }

    /** Ключ: BlockPos (блок) или UUID (чужой игрок). */
    private static final Map<Object, Receiver> RECEIVERS = new HashMap<>();
    private static final Receiver OWN = new Receiver(Kind.OWN, null, null);
    private static final List<RadioSoundInstance> RETIRING = new ArrayList<>();
    @Nullable private static Object activeKey;
    private static int tickCount;

    private RadioSoundManager() {}

    // ------------------------------------------------------------------ API

    /** Блок в мире. Вызывайте на клиенте при любом изменении состояния (загрузка, синхронизация). station == null: шум. */
    public static void update(BlockPos pos, @Nullable RadioStation station, float volume, boolean playing) {
        BlockPos key = pos.immutable();
        Receiver r = RECEIVERS.computeIfAbsent(key, k -> new Receiver(Kind.BLOCK, key, null));
        r.level = Minecraft.getInstance().level; // чтобы отличать блоки разных измерений с одинаковыми координатами
        applyState(r, station, volume, playing);
    }

    /** Вызывайте из BlockEntity.setRemoved() / onChunkUnloaded() на клиенте. */
    public static void remove(BlockPos pos) {
        removeKey(pos);
    }

    /**
     * Переносное радио ДРУГОГО игрока в режиме «динамик». Вызывайте, когда клиент получил от сервера его состояние.
     * Состояние своего игрока сюда передавать не нужно (оно игнорируется): для этого есть updateOwnRadio.
     * Если игрока нет в загруженном мире, состояние хранится ещё 10 секунд, пока он не появится.
     */
    public static void updatePlayer(UUID playerId, @Nullable RadioStation station, float volume, boolean playing) {
        Receiver r = RECEIVERS.computeIfAbsent(playerId, k -> new Receiver(Kind.PLAYER, null, playerId));
        applyState(r, station, volume, playing);
    }

    /** Игрок выключил динамик, надел наушники или убрал радио. */
    public static void removePlayer(UUID playerId) {
        removeKey(playerId);
    }

    /**
     * Собственное переносное радио локального игрока (любой режим). Вызывайте каждый тик (это дёшево) или при
     * изменении состояния предмета. playing = «радио включено»: пока true, остальные источники молчат, даже если
     * громкость 0. Когда предмета нет или он выключен, передайте playing = false.
     */
    public static void updateOwnRadio(@Nullable RadioStation station, float volume, boolean playing) {
        applyState(OWN, station, volume, playing);
    }

    /** Останавливает всё и сбрасывает состояние (в том числе собственного радио). */
    public static void stopAll() {
        for (Receiver r : RECEIVERS.values()) stopAudio(r);
        RECEIVERS.clear();
        stopAudio(OWN);
        applyState(OWN, null, 0f, false);
        for (RadioSoundInstance s : RETIRING) kill(s);
        RETIRING.clear();
        activeKey = null;
    }

    private static void removeKey(Object key) {
        Receiver r = RECEIVERS.remove(key);
        if (r != null) stopAudio(r);
        if (key.equals(activeKey)) activeKey = null;
    }

    private static void applyState(Receiver r, @Nullable RadioStation station, float volume, boolean playing) {
        if (!Objects.equals(r.stationId(), station == null ? null : station.id())) {
            r.changedAt = tickCount;
            r.failedId = null;
        }
        r.station = station;
        r.volume = Mth.clamp(volume, 0f, 1f);
        r.playing = playing;
    }

    // ------------------------------------------------------------------ события

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        tickCount++;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            if (!RECEIVERS.isEmpty() || !RETIRING.isEmpty() || OWN.playing) stopAll();
            return;
        }

        // Интернет-радио можно выключить в конфиге прямо в игре: тогда сети нет совсем, играет мелодия-затычка.
        boolean online = Config.INTERNET_RADIO.getAsBoolean();
        if (!online && !RETIRING.isEmpty()) {
            for (RadioSoundInstance s : RETIRING) kill(s);
            RETIRING.clear();
        }
        RETIRING.removeIf(s -> {
            if (s.isStopped()) { kill(s); return true; }
            return false;
        });

        // Смена измерения или мира: клиент не всегда вызывает setRemoved() у блоков прежнего уровня,
        // а координаты могут совпасть с блоками нового. Блоки чужого уровня выбрасываем сами.
        RECEIVERS.values().removeIf(r -> {
            if (r.kind != Kind.BLOCK || r.level == mc.level) return false;
            stopAudio(r);
            return true;
        });
        refreshPlayers(mc);
        if (activeKey != null && !RECEIVERS.containsKey(activeKey)) activeKey = null;

        // Если игрок убрал громкость в ноль, не тратим трафик на поток, которого никто не слышит.
        boolean muted = mc.options.getSoundSourceVolume(SoundSource.MASTER) <= 0f
                || mc.options.getSoundSourceVolume(SoundSource.RECORDS) <= 0f;

        Receiver best = null;
        if (!muted) {
            best = OWN.playing
                    ? OWN // приоритет собственного радио
                    : pickActive(mc.gameRenderer.getMainCamera().getPosition());
        }
        activeKey = best != null ? best.key() : null;

        for (Receiver r : RECEIVERS.values()) {
            if (r != best) stopAudio(r);
        }
        if (best != OWN) stopAudio(OWN);
        if (best != null) reconcile(best, mc, online);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        stopAll();
    }

    // ------------------------------------------------------------------ логика

    /** Находит сущности игроков по UUID; забывает тех, кого давно нет в мире, и нас самих. */
    private static void refreshPlayers(Minecraft mc) {
        for (Iterator<Receiver> it = RECEIVERS.values().iterator(); it.hasNext(); ) {
            Receiver r = it.next();
            if (r.kind != Kind.PLAYER) continue;
            assert mc.level != null;
            assert r.playerId != null;
            Player p = mc.level.getPlayerByUUID(r.playerId);
            if (p != null && !p.getUUID().equals(mc.player.getUUID())) {
                r.entity = p;
                r.missingTicks = 0;
            } else {
                r.entity = null;
                if (p != null || ++r.missingTicks > PLAYER_MISSING_TICKS) { // себя игнорируем сразу
                    stopAudio(r);
                    it.remove();
                }
            }
        }
    }

    /** Выбор ближайшего источника среди блоков и чужих «динамиков» (собственное радио сюда не попадает). */
    @Nullable
    private static Receiver pickActive(Vec3 listener) {
        Receiver current = activeKey == null ? null : RECEIVERS.get(activeKey);
        double currentD = Double.MAX_VALUE;
        Vec3 currentPos = current == null ? null : current.position();
        if (current != null && current.audible() && currentPos != null) {
            currentD = currentPos.distanceToSqr(listener);
            if (currentD > current.stopSq) { current = null; currentD = Double.MAX_VALUE; }
        } else {
            current = null;
        }

        Receiver best = null;
        double bestD = Double.MAX_VALUE;
        for (Receiver r : RECEIVERS.values()) {
            Vec3 pos = r.position();
            if (!r.audible() || pos == null) continue;
            double d = pos.distanceToSqr(listener);
            if (d <= r.startSq && d < bestD) { best = r; bestD = d; }
        }
        // Гистерезис: не перескакиваем на другой источник, пока он не заметно ближе (~на 20%).
        if (current != null && (best == null || bestD > currentD * 0.64)) return current;
        return best;
    }

    private static void reconcile(Receiver r, Minecraft mc, boolean online) {
        if (r.volume <= 0.001f) { // громкость в ноль: не держим соединение
            stopAudio(r);
            return;
        }
        SoundManager sm = mc.getSoundManager();
        if (!online) {
            reconcileOffline(r, sm);
            return;
        }
        if (r.melody != null) { // интернет-радио снова включили: мелодия-затычка больше не нужна
            sm.stop(r.melody);
            r.melody = null;
        }
        RadioStation st = r.station;

        // 1. Поток станции. Порядок важен: сначала подключаемся, и ТОЛЬКО когда поток готов, отдаём его движку
        // (см. RadioSoundInstance.playIn: звук, остановленный до начала воспроизведения, навсегда занимает канал).
        if (r.stream != null) {
            RadioSoundInstance s = r.stream;
            boolean wrongStation = st == null || !s.stationId().equals(st.id());

            if (s.isFailed()) { // подключиться не удалось (в движок поток так и не попал)
                if (!wrongStation) { r.failedId = st.id(); r.retryAt = tickCount + RETRY_TICKS; }
                kill(s);
                r.stream = null;
            } else if (wrongStation) { // станцию сменили
                if (s.isEngineStarted()) {
                    s.retire(); // уже звучит: плавно затихает
                    RETIRING.add(s);
                } else {
                    kill(s); // ещё подключался: просто закрываем соединение, канала движка у него нет
                }
                r.stream = null;
            } else if (!s.isEngineStarted()) { // идёт подключение
                if (s.isReady()) {
                    s.playIn(sm);
                    r.streamPlayedAt = tickCount;
                }
            } else { // звучит
                boolean refused = !mc.isPaused() && s.ticksSeen() == 0 && tickCount - r.streamPlayedAt > ACCEPT_TICKS;
                if (refused) { // движок не принял звук (например, нет свободных каналов): не долбим его, пробуем позже
                    DedNecronMod.LOGGER.warn("Radio: sound engine did not accept stream for {}", st.name());
                    r.failedId = st.id();
                    r.retryAt = tickCount + REFUSED_RETRY_TICKS;
                    kill(s);
                    r.stream = null;
                } else if (s.isStopped()) {
                    kill(s);
                    r.stream = null;
                } else {
                    s.setBaseVolume(r.volume);
                }
            }
        }

        if (st != null && st.id().equals(r.failedId) && tickCount >= r.retryAt) r.failedId = null;

        if (r.stream == null && st != null && r.failedId == null
                && tickCount - r.changedAt >= DEBOUNCE_TICKS) {
            RadioSoundInstance s = switch (r.kind) {
                case OWN -> RadioSoundInstance.headphones(st, r.volume);
                case BLOCK -> RadioSoundInstance.positional(r.pos, st, r.volume);
                case PLAYER -> RadioSoundInstance.following(r.entity, st, r.volume);
            };
            s.connect(); // движку отдадим позже, когда подключится
            r.stream = s;
        }

        // 2. Шум: нужен всегда, пока станция не зазвучала по-настоящему
        float noiseTarget = (r.stream != null && r.stream.isAudible()) ? 0f : 1f;
        if (r.noise != null && r.noise.isStopped()) r.noise = null;
        if (r.noise == null) {
            if (noiseTarget > 0f) {
                r.noise = createLoop(r, RadioLoopInstance.Loop.NOISE);
                sm.play(r.noise);
            }
        } else {
            r.noise.setTarget(noiseTarget);
            r.noise.setBaseVolume(r.volume);
        }
    }

    /**
     * Интернет-радио выключено: ни станции, ни шума, ни сети. Что бы ни выбрали на ручках и что бы ни играло
     * у другого игрока, звучит одна и та же мелодия-затычка, пока источник включён и выбран как ближайший.
     */
    private static void reconcileOffline(Receiver r, SoundManager sm) {
        if (r.stream != null) { kill(r.stream); r.stream = null; }
        if (r.noise != null) { sm.stop(r.noise); r.noise = null; }
        if (r.melody != null && r.melody.isStopped()) r.melody = null;
        if (r.melody == null) {
            r.melody = createLoop(r, RadioLoopInstance.Loop.MELODY);
            sm.play(r.melody);
        } else {
            r.melody.setBaseVolume(r.volume);
        }
    }

    private static RadioLoopInstance createLoop(Receiver r, RadioLoopInstance.Loop loop) {
        return switch (r.kind) {
            case OWN -> RadioLoopInstance.headphones(loop, r.volume);
            case BLOCK -> RadioLoopInstance.positional(loop, r.pos, r.volume);
            case PLAYER -> RadioLoopInstance.following(loop, r.entity, r.volume);
        };
    }

    private static void stopAudio(Receiver r) {
        SoundManager sm = Minecraft.getInstance().getSoundManager();
        if (r.stream != null) { kill(r.stream); r.stream = null; }
        if (r.noise != null) { sm.stop(r.noise); r.noise = null; }
        if (r.melody != null) { sm.stop(r.melody); r.melody = null; }
    }

    /** Останавливает звук в движке и закрывает сетевой поток (вторая часть критична, иначе утечка соединения). */
    private static void kill(RadioSoundInstance s) {
        if (s.isEngineStarted()) Minecraft.getInstance().getSoundManager().stop(s);
        s.shutdown();
    }

    /** Желаемое состояние одного источника звука. */
    private static final class Receiver {
        final Kind kind;
        @Nullable final BlockPos pos;        // BLOCK
        @Nullable final UUID playerId;       // PLAYER
        @Nullable final Vec3 center;         // BLOCK
        final double startSq, stopSq;
        @Nullable Object level;              // BLOCK: ClientLevel, в котором зарегистрирован блок
        @Nullable Player entity;             // PLAYER: найденная сущность (null, если игрока сейчас нет в мире)
        int missingTicks;
        @Nullable RadioStation station;
        float volume;
        boolean playing;
        int changedAt;
        @Nullable String failedId;
        int retryAt;
        int streamPlayedAt;
        @Nullable RadioSoundInstance stream;
        @Nullable RadioLoopInstance noise;
        @Nullable RadioLoopInstance melody;   // мелодия-затычка (интернет-радио выключено в конфиге)

        Receiver(Kind kind, @Nullable BlockPos pos, @Nullable UUID playerId) {
            this.kind = kind;
            this.pos = pos;
            this.playerId = playerId;
            this.center = pos == null ? null : Vec3.atCenterOf(pos);
            this.startSq = kind == Kind.PLAYER ? PLAYER_START_SQ : BLOCK_START_SQ;
            this.stopSq = kind == Kind.PLAYER ? PLAYER_STOP_SQ : BLOCK_STOP_SQ;
        }

        /** Ключ в RECEIVERS (у собственного радио ключа нет). */
        @Nullable Object key() { return kind == Kind.BLOCK ? pos : kind == Kind.PLAYER ? playerId : null; }

        /** Где сейчас источник (null: игрока нет в мире; у собственного радио позиции нет). */
        @Nullable Vec3 position() {
            return switch (kind) {
                case BLOCK -> center;
                case PLAYER -> entity == null ? null : new Vec3(entity.getX(), entity.getY() + 1.0, entity.getZ());
                case OWN -> null;
            };
        }

        @Nullable String stationId() { return station == null ? null : station.id(); }
        boolean audible() { return playing && volume > 0.001f; }
    }
}

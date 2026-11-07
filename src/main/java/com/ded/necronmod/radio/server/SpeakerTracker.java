package com.ded.necronmod.radio.server;

import com.ded.necronmod.radio.RadioSettings;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Помнит, какое состояние «динамика» уже разослано окружающим игрокам, и говорит, что нужно разослать теперь.
 * Рассылаем только изменения: нет изменений, нет пакета. Без зависимостей от Minecraft.
 */
public final class SpeakerTracker {
    private final Map<UUID, RadioSettings> current = new HashMap<>();

    /**
     * @param speakerState состояние играющего динамика игрока или null, если динамик не играет
     * @return что разослать (RadioSettings.OFF = «выключился»), или null, если ничего не изменилось
     */
    @Nullable
    public RadioSettings update(UUID id, @Nullable RadioSettings speakerState) {
        RadioSettings previous = current.get(id);
        if (Objects.equals(previous, speakerState)) return null;
        if (speakerState == null) {
            current.remove(id);
            return RadioSettings.OFF;
        }
        current.put(id, speakerState);
        return speakerState;
    }

    /** Текущее разосланное состояние игрока (для тех, кто только что его увидел) или null. */
    @Nullable
    public RadioSettings get(UUID id) { return current.get(id); }

    public void forget(UUID id) { current.remove(id); }
}

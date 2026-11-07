package com.ded.necronmod.block.entity;

import com.ded.necronmod.block.OrkInfectedBlock;
import com.ded.necronmod.init.ModBlockEntities;
import com.ded.necronmod.init.ModBlocks;
import com.ded.necronmod.util.InfectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrkTotemBlockEntity extends BlockEntity {

    private static final int SPREAD_INTERVAL_TICKS = 5; // Заражаем раз в 5 тиков (0.25 сек)
    private static final int MAX_RADIUS_SQR = 25 * 25;

    private boolean garrisonSpawned = false;
    private @Nullable UUID warbossUUID = null;
    private int tickCounter = 0;

    // Храним только активные координаты границы
    private final List<BlockPos> activeFrontier = new ArrayList<>();

    public OrkTotemBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ORK_TOTEM.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OrkTotemBlockEntity totem) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // 1. Проверка жизнеспособности Варбосса
        if (totem.warbossUUID != null) {
            var entity = serverLevel.getEntity(totem.warbossUUID);
            if (entity == null || !entity.isAlive()) {
                serverLevel.destroyBlock(pos, false);
                return;
            }
        }

        // 2. Первоначальный спавн гарнизона
        if (!totem.garrisonSpawned) {
            totem.spawnGarrison(serverLevel);
            totem.initFirstInfection(serverLevel);
            return;
        }

        // 3. Главный таймер распространения
        totem.tickCounter++;
        if (totem.tickCounter >= SPREAD_INTERVAL_TICKS) {
            totem.tickCounter = 0;
            totem.processInfectionStep(serverLevel);
        }
    }

    private void initFirstInfection(ServerLevel level) {
        this.activeFrontier.clear();

        // Заражаем 6 граней вокруг Тотема и добавляем их во фронтир
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = this.worldPosition.relative(dir);
            BlockState targetState = level.getBlockState(neighborPos);

            BlockState infectedState = InfectionManager.getInfectedVariant(targetState);
            if (infectedState != null) {
                level.setBlock(neighborPos, infectedState, 3);
                this.activeFrontier.add(neighborPos);
            }
        }
        this.setChanged();
    }

    private void processInfectionStep(ServerLevel level) {
        if (this.activeFrontier.isEmpty()) return;

        // Выбираем случайную точку границы
        int index = level.random.nextInt(this.activeFrontier.size());
        BlockPos currentPos = this.activeFrontier.get(index);
        BlockState currentState = level.getBlockState(currentPos);

        // Если блок перестали считаться зараженным — удаляем из очереди
        if (!InfectionManager.isInfected(currentState)) {
            this.activeFrontier.remove(index);
            return;
        }

        // А) Повышаем стадию блока
        int stage = currentState.getValue(OrkInfectedBlock.STAGE);
        if (stage < 3 && level.random.nextFloat() < 0.25F) {
            level.setBlock(currentPos, currentState.setValue(OrkInfectedBlock.STAGE, stage + 1), 3);
        }

        // Б) Заражаем одного из соседей
        Direction randomDir = getRandomWeightedDirection(level.random);
        BlockPos targetPos = currentPos.relative(randomDir);

        // Проверяем радиус от Тотема
        if (targetPos.distSqr(this.worldPosition) <= MAX_RADIUS_SQR) {
            BlockState targetState = level.getBlockState(targetPos);
            BlockState infectedState = InfectionManager.getInfectedVariant(targetState);

            if (infectedState != null) {
                level.setBlock(targetPos, infectedState, 3);
                this.activeFrontier.add(targetPos); // Новый блок становится частью фронтира!
            }
        }

        // В) Если у текущего блока все соседи уже заражены — удаляем его из очереди (он в центре)
        if (isFullySurrounded(level, currentPos)) {
            this.activeFrontier.remove(index);
        }

        this.setChanged();
    }

    private boolean isFullySurrounded(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockState neighborState = level.getBlockState(pos.relative(dir));
            if (!InfectionManager.isInfected(neighborState) && !neighborState.is(ModBlocks.ORK_TOTEM.get())) {
                return false;
            }
        }
        return true;
    }

    private Direction getRandomWeightedDirection(net.minecraft.util.RandomSource random) {
        if (random.nextFloat() < 0.85F) {
            return Direction.Plane.HORIZONTAL.getRandomDirection(random);
        }
        return random.nextBoolean() ? Direction.UP : Direction.DOWN;
    }

    // --- СПАВН ГАРНИЗОНА ---
    private <T extends Mob> void spawnGarrisonMember(ServerLevel level, EntityType<T> entityType, BlockPos pos, String customName) {
        T mob = entityType.create(level, null, pos, MobSpawnType.STRUCTURE, true, false);
        if (mob != null) {
            mob.setYRot(level.random.nextFloat() * 360.0F);
            if (customName != null) mob.setCustomName(Component.literal(customName));
            level.addFreshEntity(mob);

            if (entityType == EntityType.ZOMBIE && this.warbossUUID == null) {
                this.warbossUUID = mob.getUUID();
            }
        }
    }

    private void spawnGarrison(ServerLevel level) {
        spawnGarrisonMember(level, EntityType.ZOMBIE, this.worldPosition.above(), "Орк Варбосс");

        for (int i = 0; i < 3; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 6.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 6.0;
            BlockPos nobPos = BlockPos.containing(this.worldPosition.getX() + offsetX, this.worldPosition.getY() + 1.0, this.worldPosition.getZ() + offsetZ);
            spawnGarrisonMember(level, EntityType.ZOMBIE, nobPos, "Ноб-Телохранитель");
        }
        this.garrisonSpawned = true;
    }

    // --- NBT СОХРАНЕНИЕ СПИСКА КООРДИНАТ ---
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("GarrisonSpawned", this.garrisonSpawned);

        if (this.warbossUUID != null) {
            tag.putUUID("WarbossUUID", this.warbossUUID);
        }

        // Сохраняем позиции фронтира в NBT
        ListTag frontierTag = new ListTag();
        for (BlockPos pos : this.activeFrontier) {
            frontierTag.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("ActiveFrontier", frontierTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.garrisonSpawned = tag.getBoolean("GarrisonSpawned");

        if (tag.hasUUID("WarbossUUID")) {
            this.warbossUUID = tag.getUUID("WarbossUUID");
        }

        // Загружаем позиции фронтира из NBT
        this.activeFrontier.clear();
        if (tag.contains("ActiveFrontier", Tag.TAG_LIST)) {
            ListTag frontierTag = tag.getList("ActiveFrontier", Tag.TAG_COMPOUND);
            for (int i = 0; i < frontierTag.size(); i++) {
                NbtUtils.readBlockPos(frontierTag.getCompound(i), "pos").ifPresent(this.activeFrontier::add);
            }
        }
    }
}
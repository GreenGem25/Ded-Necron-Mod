package com.ded.necronmod.block.entity;

import com.ded.necronmod.init.ModBlockEntities;
import com.ded.necronmod.radio.RadioSettings;
import com.ded.necronmod.radio.RadioSettingsCodecs;
import com.ded.necronmod.radio.client.RadioClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Хранит настройки радио-блока (страна, позиция, громкость, «включено») и синхронизирует их клиентам.
 * Сервер звука не знает: на клиентах эти данные превращаются в звук через RadioClient -> RadioSoundManager.
 */
public class RadioBlockEntity extends BlockEntity {
    private static final String TAG = "Radio";

    private RadioSettings settings = RadioSettings.DEFAULT;

    public RadioBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RADIO_BLOCK_ENTITY.get(), pos, state);
    }

    public RadioSettings getSettings() { return settings; }

    /** Сервер: применить настройки (уже проверенные RadioServer), сохранить и разослать клиентам вокруг. */
    public void applySettings(RadioSettings next) {
        RadioSettings clean = next.sanitized(false);
        if (clean.equals(settings)) return;
        settings = clean;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /** Клиент: сразу показать собственную правку, не дожидаясь подтверждения сервера. */
    public void setSettingsClient(RadioSettings next) { settings = next; }

    // ------------------------------------------------------------------ сохранение и синхронизация

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        RadioSettingsCodecs.CODEC.encodeStart(NbtOps.INSTANCE, settings).result().ifPresent(t -> tag.put(TAG, t));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG)) {
            RadioSettingsCodecs.CODEC.parse(NbtOps.INSTANCE, tag.get(TAG)).result()
                    .ifPresent(s -> settings = s.sanitized(false));
        }
    }

    /** Данные при загрузке чанка. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    /** Данные при sendBlockUpdated. */
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ------------------------------------------------------------------ клиентские уведомления (звук)

    /** Блок добавлен в мир (в том числе при получении чанка клиентом). loadAdditional в этот момент level ещё не знает. */
    @Override
    public void onLoad() {
        super.onLoad();
        notifyClient();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        notifyClient();
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
        notifyClient();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (isClientLevel()) RadioClient.blockRemoved(worldPosition);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (isClientLevel()) RadioClient.blockRemoved(worldPosition);
    }

    private boolean isClientLevel() { return level != null && level.isClientSide; }

    /** Вызывает клиентский класс только на клиенте: на сервере эта ветка недостижима. */
    private void notifyClient() {
        if (isClientLevel()) RadioClient.blockChanged(worldPosition, settings);
    }
}

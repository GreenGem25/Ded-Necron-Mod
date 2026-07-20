package com.ded.necronmod.block.entity;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChunkLoaderObeliskBlockEntity extends BlockEntity {

    public ChunkLoaderObeliskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHUNK_LOADER_OBELISK.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide() && this.level instanceof ServerLevel serverLevel) {
            setChunkLoading(serverLevel, true);
        }
    }

    public void releaseChunk() {
        if (this.level != null && !this.level.isClientSide() && this.level instanceof ServerLevel serverLevel) {
            setChunkLoading(serverLevel, false);
        }
    }

    private void setChunkLoading(ServerLevel serverLevel, boolean force) {
        ChunkPos chunkPos = new ChunkPos(this.worldPosition);

        // Передаем параметры:
        // level, owner (BlockPos), chunkX, chunkZ, add (включаем или выключаем?), ticking
        DedNecronMod.OBELISK_TICKET_CONTROLLER.forceChunk(
                serverLevel,
                this.worldPosition,
                chunkPos.x,
                chunkPos.z,
                force, // true добавит билет, false — удалит его
                true   // ticking (даем тики блокам/редстоуну/мобам)
        );
    }

    @Override
    public void setRemoved() {
        releaseChunk();
        super.setRemoved();
    }
}
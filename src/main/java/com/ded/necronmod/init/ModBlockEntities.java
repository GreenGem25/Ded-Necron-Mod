package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.block.entity.ChunkLoaderObeliskBlockEntity;
import com.ded.necronmod.block.entity.OrkTotemBlockEntity;
import com.ded.necronmod.block.entity.RadioBlockEntity;
import com.ded.necronmod.block.entity.TowerTopBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DedNecronMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TowerTopBlockEntity>> TOWER_TOP =
            BLOCK_ENTITIES.register("tower_top", () ->
                    BlockEntityType.Builder.of(
                            TowerTopBlockEntity::new,
                            ModBlocks.TOWER_TOP.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChunkLoaderObeliskBlockEntity>> CHUNK_LOADER_OBELISK =
            BLOCK_ENTITIES.register("chunk_loader_obelisk",
                    () -> BlockEntityType.Builder.of(ChunkLoaderObeliskBlockEntity::new,
                            ModBlocks.CHUNK_LOADER_OBELISK_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OrkTotemBlockEntity>> ORK_TOTEM =
            BLOCK_ENTITIES.register("ork_totem",
                    () -> BlockEntityType.Builder.of(OrkTotemBlockEntity::new,
                            ModBlocks.ORK_TOTEM.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadioBlockEntity>> RADIO_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("radio_block_entity",
                    () -> BlockEntityType.Builder.of(RadioBlockEntity::new,
                            ModBlocks.RADIO_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
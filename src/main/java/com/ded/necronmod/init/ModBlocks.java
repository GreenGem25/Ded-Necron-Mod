package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DedNecronMod.MODID);

    public static final DeferredBlock<Block> MONOLITH_SPROUT = BLOCKS.register("monolith_sprout",
            MonolithSprout::new);

    public static final DeferredHolder<Block, NapoleonCake> NAPOLEON_CAKE = BLOCKS.register("napoleon_cake",
            () -> new NapoleonCake(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)));

    public static final DeferredHolder<Block, Block> SHLAKOBLOCK = BLOCKS.register("shlakoblock",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_CONCRETE)));

    public static final DeferredHolder<Block, SteklovataBlock> STEKLOVATA_BLOCK = BLOCKS.register("steklovata_block",
            () -> new SteklovataBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK)));

    public static final DeferredHolder<Block, RottenFleshBlock> ROTTEN_FLESH_BLOCK = BLOCKS.register("rotten_flesh_block",
            () -> new RottenFleshBlock(BlockBehaviour.Properties.of()
                    .strength(0.4F, 3.0F)
                    .friction(1.0F)
                    .speedFactor(1.0F)
                    .jumpFactor(1.1F)
                    .sound(SoundType.HONEY_BLOCK)
            ));

    public static final DeferredHolder<Block, NecronTombBlock> NECRON_TOMB_BLOCK = BLOCKS.register("necron_tomb_block",
            () -> new NecronTombBlock(BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.0F)
                    .requiresCorrectToolForDrops()
            ));

    public static final DeferredHolder<Block, Block> CHUNK_LOADER_OBELISK_BLOCK = BLOCKS.register("chunk_loader_obelisk_block",
            () -> new ChunkLoaderObeliskBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)
                    .strength(4.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
            ));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

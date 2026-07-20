package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.worldgen.structure.CommunicationsTowerStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, DedNecronMod.MODID);

    public static final DeferredRegister<StructurePieceType> PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, DedNecronMod.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<CommunicationsTowerStructure>> COMMUNICATIONS_TOWER =
            STRUCTURE_TYPES.register("communications_tower", () -> () -> CommunicationsTowerStructure.CODEC);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> TOWER_PIECE_TYPE =
            PIECE_TYPES.register("tower_piece", () -> (context, tag) -> new CommunicationsTowerStructure.TowerPiece(tag));

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
        PIECE_TYPES.register(eventBus);
    }
}
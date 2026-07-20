package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, DedNecronMod.MODID);

    public static final Holder<ArmorMaterial> NECRODERMIS_ARMOR_MATERIAL = ARMOR_MATERIALS.register("necro_material",
            () -> {
                EnumMap<ArmorItem.Type, Integer> defense = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                   map.put(ArmorItem.Type.BOOTS, 6);
                   map.put(ArmorItem.Type.LEGGINGS, 12);
                   map.put(ArmorItem.Type.CHESTPLATE, 16);
                   map.put(ArmorItem.Type.HELMET, 7);
                });

                return new ArmorMaterial(
                        defense,
                        15, // Enchantability
                        SoundEvents.ARMOR_EQUIP_DIAMOND,
                        () -> Ingredient.of(ModItems.LIVING_METAL_PLATE.get()), // Anvil repair item
                        List.of(new ArmorMaterial.Layer(ResourceLocation.
                                fromNamespaceAndPath(DedNecronMod.MODID, "necro_armor"))),
                        5.0F, // Toughness
                        0.2F // KB Resistance
                );
            });

    public static final Holder<ArmorMaterial> POOP_ARMOR_MATERIAL = ARMOR_MATERIALS.register("poop_material",
            () -> {
                EnumMap<ArmorItem.Type, Integer> defense = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 2);
                });

                return new ArmorMaterial(
                        defense,
                        1, // Enchantability
                        SoundEvents.ARMOR_EQUIP_LEATHER,
                        () -> Ingredient.of(Items.COCOA_BEANS), // Anvil repair item
                        List.of(new ArmorMaterial.Layer(ResourceLocation.
                                fromNamespaceAndPath(DedNecronMod.MODID, "poop_armor"))),
                        0.1F, // Toughness
                        0.0F // KB Resistance
                );
            });

    public static final Holder<ArmorMaterial> TIN_FOIL_ARMOR_MATERIAL = ARMOR_MATERIALS.register("tin_foil_material",
            () -> {
                EnumMap<ArmorItem.Type, Integer> defense = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.HELMET, 2);
                });

                return new ArmorMaterial(
                        defense,
                        1, // Enchantability
                        SoundEvents.ARMOR_EQUIP_LEATHER,
                        () -> Ingredient.of(Items.IRON_NUGGET), // Anvil repair item
                        List.of(new ArmorMaterial.Layer(ResourceLocation.
                                fromNamespaceAndPath(DedNecronMod.MODID, "tin_foil_armor"))),
                        0.1F, // Toughness
                        0.0F // KB Resistance
                );
            });

    public static final Holder<ArmorMaterial> STEKLOVATA_ARMOR_MATERIAL = ARMOR_MATERIALS.register("steklovata_material",
            () -> {
                EnumMap<ArmorItem.Type, Integer> defense = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 1);
                    map.put(ArmorItem.Type.LEGGINGS, 2);
                    map.put(ArmorItem.Type.CHESTPLATE, 4);
                    map.put(ArmorItem.Type.HELMET, 3);
                });

                return new ArmorMaterial(
                        defense,
                        1, // Enchantability
                        SoundEvents.ARMOR_EQUIP_LEATHER,
                        () -> Ingredient.of(ModItems.STEKLOVATA.get()), // Anvil repair item
                        List.of(new ArmorMaterial.Layer(ResourceLocation.
                                fromNamespaceAndPath(DedNecronMod.MODID, "steklovata_armor"))),
                        1.0F, // Toughness
                        0.0F // KB Resistance
                );
            });

    public static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
    }
}

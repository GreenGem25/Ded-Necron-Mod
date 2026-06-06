package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.NecronArmorItem;
import com.ded.necronmod.Item.NecronStaffitem;
import com.ded.necronmod.Item.PoopHelmetItem;
import com.ded.necronmod.Item.UssrCannedFishItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, DedNecronMod.MODID);

    public static final DeferredHolder<Item, Item> SLON_MUSIC_DISC = ITEMS.register("slon_music_disc",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .jukeboxPlayable(ResourceKey.create(
                            Registries.JUKEBOX_SONG,
                            ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "slon_music")
                    ))));

    public static final DeferredHolder<Item, NecronArmorItem> NECRODERMIS_HELMET = ITEMS.register("necro_helmet",
            () -> new NecronArmorItem(ModArmorMaterials.NECRODERMIS_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(1)));

    public static final DeferredHolder<Item, NecronArmorItem> NECRODERMIS_CHESTPLATE = ITEMS.register("necro_chestplate",
            () -> new NecronArmorItem(ModArmorMaterials.NECRODERMIS_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(1)));

    public static final DeferredHolder<Item, NecronArmorItem> NECRODERMIS_LEGGINGS = ITEMS.register("necro_leggings",
            () -> new NecronArmorItem(ModArmorMaterials.NECRODERMIS_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(1)));

    public static final DeferredHolder<Item, NecronArmorItem> NECRODERMIS_BOOTS = ITEMS.register("necro_boots",
            () -> new NecronArmorItem(ModArmorMaterials.NECRODERMIS_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(1)));

    public static final DeferredHolder<Item, NecronStaffitem> NECRON_STAFF = ITEMS.register("necron_staff",
            () -> new NecronStaffitem(
                    ModToolTiers.NECRO, // 1. Передаем наш Тир материала
                    new Item.Properties().attributes(createSwordAttributes()) // 2. Настраиваем урон и скорость через атрибуты свойства
            ));

    // хлеб из опилок
    public static final DeferredHolder<Item, Item> SAWDUST_BREAD = ITEMS.register("sawdust_bread",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 0), 1.0F)
                            .build()
            )));

    // Шаурма (голод, тошноат)
    public static final DeferredHolder<Item, Item> SHAURMA = ITEMS.register("shaurma",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(10)
                            .saturationModifier(1.2F)
                            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.8F)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 0), 0.6F)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1), 0.8F)
                            .build()
            )));

    // Пирожок с картофелем (отравл)
    public static final DeferredHolder<Item, Item> POTATO_PIROZHOK = ITEMS.register("potato_pirozhok",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(5)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 1), 1.0F)
                            .build()
            )));

    // Коснервы времен ссср
    public static final DeferredHolder<Item, Item> USSR_CANNED_FISH = ITEMS.register("ussr_canned_fish",
            () -> new UssrCannedFishItem(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(5)
                            .saturationModifier(0.5F)
                            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.WITHER, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.BLINDNESS, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.INFESTED, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 200, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 1), 1.0F)
                            .build()
            )));

    // пирог из некродермиса
    public static final DeferredHolder<Item, Item> NECRODERMIS_PIE = ITEMS.register("necrodermis_pie",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(10)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0F)
                            .build()
            )));

    // пирог из булыжнка
    public static final DeferredHolder<Item, Item> COBBLESTONE_PIE = ITEMS.register("cobblestone_pie",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(5)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1), 1.0F)
                            .build()
            )));

    // пирог из андезита с базальтовой посыпкой
    public static final DeferredHolder<Item, Item> COBBLESTONE_PIE_ENHANCED = ITEMS.register("cobblestone_pie_enhanced",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(7)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 1), 1.0F)
                            .build()
            )));

    // редстоуновая каша
    public static final DeferredHolder<Item, Item> REDSTONE_SOUP = ITEMS.register("redstone_soup",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(6)
                            .saturationModifier(1.0F)
                            .usingConvertsTo(Items.BOWL)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 3600, 1), 1.0F)
                            .build()
                    )));

    // суп из опилок
    public static final DeferredHolder<Item, Item> SAWDUST_SOUP = ITEMS.register("sawdust_soup",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(1.0F)
                            .usingConvertsTo(Items.BOWL)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 100, 0), 1.0F)
                            .build()
                    )));

    // бамбуковая сигарета
    public static final DeferredHolder<Item, Item> BAMBOO_CIGARETTE = ITEMS.register("bamboo_cigarette",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(1)
                            .saturationModifier(1.0F)
                            .fast()
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(ModEffects.LUNG_CANCER_EFFECT, 9999, 1), 1.0F)
                            .build()
            )){
                @Override
                public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

                    tooltipComponents.add(Component.empty());

                    tooltipComponents.add(Component.translatable("tooltip.necronmod.bamboo_cigarette_desc")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredHolder<Item, Item> MONOLITH_SEEDS = ITEMS.register("monolith_seeds",
            () -> new ItemNameBlockItem(ModBlocks.MONOLITH_SPROUT.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> LIVING_METAL_PLATE = ITEMS.register("living_metal_plate",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, PoopHelmetItem> POOP_HELMET = ITEMS.register("poop_helmet",
            () -> new PoopHelmetItem(ModArmorMaterials.POOP_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(120)));

    // питьевой клей
    // кака кола
    // жевачка со вкусом наждачки
    // шлакоблок
    // шлакоблокунь
    // стекловата
    // черемша
    // мясо ендермена

    private static ItemAttributeModifiers createSwordAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        ResourceLocation.withDefaultNamespace("base_attack_damage"),
                        (float) 5.0 + ModToolTiers.NECRO.getAttackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(
                        ResourceLocation.withDefaultNamespace("base_attack_speed"),
                        (float) -0.4,
                        AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}

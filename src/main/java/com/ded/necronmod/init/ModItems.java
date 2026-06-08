package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.Item.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.Unbreakable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SimpleTier;
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
                    ModToolTiers.NECRO,
                    new Item.Properties().attributes(createSwordAttributes(0.7F, ModToolTiers.NECRO))
            ));

    public static final DeferredHolder<Item, Item> SAWDUST_BREAD = ITEMS.register("sawdust_bread",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 200, 0), 1.0F)
                            .build()
            )));

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
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 1.0F)
                            .build()
            )));

    public static final DeferredHolder<Item, Item> NECRODERMIS_PIE = ITEMS.register("necrodermis_pie",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(10)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0F)
                            .build()
            )));

    public static final DeferredHolder<Item, Item> COBBLESTONE_PIE = ITEMS.register("cobblestone_pie",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(6)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1), 1.0F)
                            .build()
            )));

    public static final DeferredHolder<Item, Item> COBBLESTONE_PIE_ENHANCED = ITEMS.register("cobblestone_pie_enhanced",
            () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1), 1.0F)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 1), 1.0F)
                            .build()
            )));

    public static final DeferredHolder<Item, Item> REDSTONE_PORRIDGE = ITEMS.register("redstone_porridge",
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

    public static final DeferredHolder<Item, Item> SAWDUST_SOUP = ITEMS.register("sawdust_soup",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .food(new FoodProperties.Builder()
                            .nutrition(5)
                            .saturationModifier(1.0F)
                            .usingConvertsTo(Items.BOWL)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 100, 0), 1.0F)
                            .build()
                    )));

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

    public static final DeferredHolder<Item, Item> STEKLOVATA = ITEMS.register("steklovata",
            () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, Item> STEKLOVATA_CANDY = ITEMS.register("steklovata_candy",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .food(new FoodProperties.Builder()
                            .nutrition(3)
                            .saturationModifier(0.5F)
                            .effect(() -> new MobEffectInstance(MobEffects.HARM, 20, 0), 1.0F)
                            .build()
                    )));

    public static final DeferredHolder<Item, Item> CORPSE_STARCH_BAR = ITEMS.register("corpse_starch_bar",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(5)
                            .saturationModifier(0.7F)
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 100, 1), 0.5F)
                            .build()
                    )));

    public static final DeferredHolder<Item, BlockItem> NAPOLEON_CAKE_ITEM = ITEMS.register("napoleon_cake",
            () -> new BlockItem(ModBlocks.NAPOLEON_CAKE.get(), new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> PERCH = ITEMS.register("perch",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationModifier(0.6F)
                            .build()
                    )));

    public static final DeferredHolder<Item, Item> COOKED_PERCH = ITEMS.register("cooked_perch",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(6)
                            .saturationModifier(0.8F)
                            .build()
                    )));

    public static final DeferredHolder<Item, BlockItem> SHLAKOBLOCK = ITEMS.register("shlakoblock",
            () -> new BlockItem(ModBlocks.SHLAKOBLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> SHLAKOBLOKUN = ITEMS.register("shlakoblokun",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(6)
                            .saturationModifier(0.8F)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2), 1.0F)
                            .build()
                    )));

    public static final DeferredHolder<Item, Item> STEKLOVATA_HELMET = ITEMS.register("steklovata_helmet",
            () -> new SteklovataArmorItem(ModArmorMaterials.STEKLOVATA_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(15))));

    public static final DeferredHolder<Item, Item> STEKLOVATA_CHESTPLATE = ITEMS.register("steklovata_chestplate",
            () -> new SteklovataArmorItem(ModArmorMaterials.STEKLOVATA_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(15))));

    public static final DeferredHolder<Item, Item> STEKLOVATA_LEGGINGS = ITEMS.register("steklovata_leggings",
            () -> new SteklovataArmorItem(ModArmorMaterials.STEKLOVATA_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(15))));

    public static final DeferredHolder<Item, Item> STEKLOVATA_BOOTS = ITEMS.register("steklovata_boots",
            () -> new SteklovataArmorItem(ModArmorMaterials.STEKLOVATA_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(15))));

    public static final DeferredHolder<Item, BlockItem> STEKLOVATA_BLOCK = ITEMS.register("steklovata_block",
            () -> new BlockItem(ModBlocks.STEKLOVATA_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> ROTTEN_FLESH_BLOCK = ITEMS.register("rotten_flesh_block",
            () -> new BlockItem(ModBlocks.ROTTEN_FLESH_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, SwordItem> SHAMPUR = ITEMS.register("shampur",
            () -> new SwordItem(
                    ModToolTiers.SHAMPUR,
                    new Item.Properties().component(DataComponents.UNBREAKABLE,
                            new Unbreakable(false)).attributes(createSwordAttributes(0.5F, ModToolTiers.SHAMPUR))
            ));

    public static final DeferredHolder<Item, Item> SHASHLIK = ITEMS.register("shashlik",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(1.0F)
                            .usingConvertsTo(SHAMPUR.get())
                            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 160, 1), 0.2F)
                            .build()
                    )));

    // питьевой клей
    // жевачка со вкусом наждачки
    // мясо ендермена
    // Пояс шахида
    // скарабейчики пчелы
    // фпв дрон пчела
    // люля кебаб из мяса эндермана
    // сладкая вата из лисов лесей лисов
    // кровать из стекловаты

    private static ItemAttributeModifiers createSwordAttributes(float speed, Tier tier) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                        ResourceLocation.withDefaultNamespace("base_attack_damage"),
                        tier.getAttackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(
                        ResourceLocation.withDefaultNamespace("base_attack_speed"),
                        -speed,
                        AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}

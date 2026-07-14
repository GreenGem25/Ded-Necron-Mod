package com.ded.necronmod;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue NECRO_ARMOR_SET_DAMAGE_REDUCE = BUILDER
            .comment("Multiplier applied to all incoming damage. Default: 0.5")
            .translation("necronmod.configuration.necroArmorSetDamageReduction")
            .defineInRange("necroArmorSetDamageReduction", 0.5F, 0.0F, 1.0F);

    public static final ModConfigSpec.IntValue NECRO_ARMOR_DURABILITY = BUILDER
            .comment("Necron armor durability. Default: 1488")
            .translation("necronmod.configuration.necroArmorDurability")
            .defineInRange("necroArmorDurability", 1488, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue NECRO_STAFF_DURABILITY = BUILDER
            .comment("Necron staff durability. Default: 1488")
            .translation("necronmod.configuration.necroStaffDurability")
            .defineInRange("necroStaffDurability", 1488, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue NECRO_ITEMS_REPAIR_POWER = BUILDER
            .comment("The durability added to necron items by Phylactery. Default: 1")
            .translation("necronmod.configuration.necroItemsRepairPower")
            .defineInRange("necroItemsRepairPower", 1, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue PHYLACTERY_EFFECT_COOLDOWN = BUILDER
            .comment("How fast in ticks effect applies. Default: 100")
            .translation("necronmod.configuration.phylacteryEffectCooldown")
            .defineInRange("phylacteryEffectCooldown", 100, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue SHOW_PHYLACTERY_PARTICLES = BUILDER
            .comment("Should potion particles be visible. Default: true")
            .translation("necronmod.configuration.showPhylacteryParticles")
            .define("showPhylacteryParticles", true);

    static final ModConfigSpec SPEC = BUILDER.build();

}

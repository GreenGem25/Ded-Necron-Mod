package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.effect.LungCancerEffect;
import com.ded.necronmod.effect.PhylacteryEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, DedNecronMod.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> PHYLACTERY_EFFECT = MOB_EFFECTS.register("phylactery_effect",
            PhylacteryEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> LUNG_CANCER_EFFECT = MOB_EFFECTS.register("lung_cancer_effect",
            LungCancerEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}

package com.ded.necronmod.init;

import com.ded.necronmod.DedNecronMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, DedNecronMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SLON_MUSIC = SOUNDS.register("slon_music",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "slon_music")));

    public static final DeferredHolder<SoundEvent, SoundEvent> COUGHING_SOUND = SOUNDS.register("coughing_sound",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "coughing_sound")));

    public static final DeferredHolder<SoundEvent, SoundEvent> USSR_CANNED_FISH_SOUND = SOUNDS.register("ussr_canned_fish_sound",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "ussr_canned_fish_sound")));

    public static final DeferredHolder<SoundEvent, SoundEvent> NECRON_STAFF_SHOT = SOUNDS.register("necron_staff_shot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "necron_staff_shot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CAT_CATERPILLAR_AMBIENT = SOUNDS.register("cat_caterpillar_ambient",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "cat_caterpillar_ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CAT_CATERPILLAR_SIT = SOUNDS.register("cat_caterpillar_sit",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "cat_caterpillar_sit")));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}

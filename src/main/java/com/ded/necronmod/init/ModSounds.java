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

    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTER = SOUNDS.register("lighter",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "lighter")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BHOP_MUSIC = SOUNDS.register("bhop_music",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "bhop_music")));

    public static final DeferredHolder<SoundEvent, SoundEvent> USSR_CANNED_FISH_SOUND = SOUNDS.register("ussr_canned_fish_sound",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "ussr_canned_fish_sound")));

    public static final DeferredHolder<SoundEvent, SoundEvent> NECRON_STAFF_SHOT = SOUNDS.register("necron_staff_shot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "necron_staff_shot")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CAT_CATERPILLAR_AMBIENT = SOUNDS.register("cat_caterpillar_ambient",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "cat_caterpillar_ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CAT_CATERPILLAR_SIT = SOUNDS.register("cat_caterpillar_sit",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "cat_caterpillar_sit")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BRAINROT_SOUND = SOUNDS.register("brainrot_sound",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "brainrot_sound")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BRAINROT_SOUND_2 = SOUNDS.register("brainrot_sound_2",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "brainrot_sound_2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BRAINROT_SOUND_3 = SOUNDS.register("brainrot_sound_3",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "brainrot_sound_3")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RADIO_NOISE = SOUNDS.register("radio_noise",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_noise")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RADIO_STREAM = SOUNDS.register("radio_stream",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_stream")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RADIO_NOISE_PORTABLE = SOUNDS.register("radio_noise_portable",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_noise_portable")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RADIO_STREAM_PORTABLE = SOUNDS.register("radio_stream_portable",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_stream_portable")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RADIO_MELODY = SOUNDS.register("radio_melody",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_melody")));

    public static final DeferredHolder<SoundEvent, SoundEvent> RADIO_MELODY_PORTABLE = SOUNDS.register("radio_melody_portable",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DedNecronMod.MODID, "radio_melody_portable")));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}

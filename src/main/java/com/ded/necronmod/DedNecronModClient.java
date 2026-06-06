package com.ded.necronmod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = DedNecronMod.MODID, dist = Dist.CLIENT)

@EventBusSubscriber(modid = DedNecronMod.MODID)

public class DedNecronModClient {

    public DedNecronModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        DedNecronMod.LOGGER.info("HELLO FROM DED NECRON MOD CLIENT SETUP");
    }

}

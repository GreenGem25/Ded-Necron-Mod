package com.ded.necronmod;

import com.ded.necronmod.block.entity.ChunkLoaderObeliskBlockEntity;
import com.ded.necronmod.init.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(DedNecronMod.MODID)
public class DedNecronMod {

    public static final String MODID = "necronmod";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final TicketController OBELISK_TICKET_CONTROLLER = new TicketController(
            ResourceLocation.fromNamespaceAndPath("necronmod", "obelisk_chunks"),
            (level, ticketHelper) -> {
                // Валидатор: при запуске мира проверяем, стоит ли по-прежнему наш блок по координатам билета
                ticketHelper.getBlockTickets().keySet().forEach(pos -> {
                    // Если блока на месте нет (например, его убрали сторонней программой или сетом)
                    if (!(level.getBlockEntity(pos) instanceof ChunkLoaderObeliskBlockEntity)) {
                        ticketHelper.removeAllTickets(pos); // Удаляем билеты для этой позиции
                    }
                });
            }
    );

    public DedNecronMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(RegisterTicketControllersEvent.class,
                event -> event.register(OBELISK_TICKET_CONTROLLER));

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModArmorMaterials.register(modEventBus);
        ModEffects.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModDataComponentTypes.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM DED NECRON MOD COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}

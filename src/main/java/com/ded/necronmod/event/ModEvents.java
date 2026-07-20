package com.ded.necronmod.event;

import com.ded.necronmod.DedNecronMod;
import com.ded.necronmod.init.ModEntities;
import com.ded.necronmod.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = DedNecronMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {

        if (!event.getLeft().is(ModItems.CHUGUN_SAW.get())) {
            return;
        }
        System.out.println("ANVIL UPDATE");
        System.out.println("LEFT = " + event.getLeft());
        System.out.println("RIGHT = " + event.getRight());
        ItemStack right = event.getRight();

        if (!right.has(DataComponents.STORED_ENCHANTMENTS)) {
            return;
        }

        var enchants = right.get(DataComponents.STORED_ENCHANTMENTS);

        assert enchants != null;
        boolean allowed = enchants.entrySet().stream()
                .allMatch(entry ->
                        entry.getKey().is(Enchantments.UNBREAKING)
                                || entry.getKey().is(Enchantments.MENDING));

        if (!allowed) {
            event.setOutput(ItemStack.EMPTY);
            event.setCanceled(true);
            System.out.println("SHOULD RETURN EMPTY");
        }
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.CAT_CATERPILLAR.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR
        );

        event.register(
                ModEntities.TARAKAN.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.OR
        );
    }
}

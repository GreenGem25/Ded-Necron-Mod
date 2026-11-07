package com.ded.necronmod.radio;

import com.ded.necronmod.item.PortableRadioItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Какое переносное радио у игрока «играет». ЕДИНОЕ правило для клиента (звук владельцу) и сервера (рассылка
 * динамика другим), чтобы они никогда не разошлись: первое по порядку слотов радио, которое включено.
 * Когда добавите Curios, поменяется только этот метод.
 */
public final class RadioInventory {
    private RadioInventory() {}

    /** @return стек играющего радио или ItemStack.EMPTY */
    public static ItemStack findActive(Player player) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) { // основные слоты, броня, вторая рука
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof PortableRadioItem && PortableRadioItem.settings(stack).playing()) return stack;
        }
        return ItemStack.EMPTY;
    }
}

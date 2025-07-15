package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.util.TriConsumer;

public class ServerUtilities {

    public static void forBatteryInItems(LivingEntity entity, TriConsumer<ItemStack, LivingEntity, Integer> consumer) {
        IItemHandler capability = entity.getCapability(Capabilities.ItemHandler.ENTITY);

        if (capability != null) {
            for (int i = 0; i < capability.getSlots(); i++) {
                ItemStack stack = capability.getStackInSlot(i);

                if (stack.has(LysateCell.BATTERY_CELL_COMPONENT)) {
                    consumer.accept(stack, entity, i);
                }
            }
        }
    }
}

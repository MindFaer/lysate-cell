package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.logging.log4j.util.TriConsumer;

public class ServerUtilities {

    public static void forBatteryInItems(LivingEntity entity, TriConsumer<ItemStack, LivingEntity, OmniSlot> consumer) {
        IItemHandler cap = entity.getCapability(Capabilities.ItemHandler.ENTITY);

        if (cap != null) {
            for (int i = 0; i < cap.getSlots(); i++) {
                ItemStack stack = cap.getStackInSlot(i);

                if (stack.has(LysateCell.BATTERY_CELL_COMPONENT)) {
                    consumer.accept(stack, entity, OmniSlot.capability(i));
                }
            }
        }
    }
}

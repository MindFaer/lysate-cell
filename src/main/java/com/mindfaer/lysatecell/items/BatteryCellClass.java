package com.mindfaer.lysatecell.items;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.mindfaer.lysatecell.items.BatteryCellDataHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import com.mindfaer.lysatecell.LysateCell;

public class BatteryCellClass {
    public static void inventoryTick(ItemStack itemStack, LivingEntity entity, OmniSlot slot) {
        BatteryCellDataHandler batterycellclass = itemStack.get(BatteryCellDataHandler.BATTERY_CELL_COMPONENT);
        assert batterycellclass != null;

        batterycellclass = batterycellclass.withHeldByOwner(isOwner(batterycellclass, entity));
        itemStack.set(BatteryCellDataHandler.BATTERY_CELL, batterycellclass);

        if (isOwner(batterycellclass, entity)) {
            IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);

            if (energyStorage != null) {
                energyStorage.receiveEnergy(Mth.square(batterycellclass.level() + 1), false);
            }
        }
    }
}

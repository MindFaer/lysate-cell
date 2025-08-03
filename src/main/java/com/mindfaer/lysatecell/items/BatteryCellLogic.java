package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

public class BatteryCellLogic {

    public static void inventoryTick(ItemStack stack, LivingEntity entity, int slotId) {
        var energyCapability = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        var batteryCheck = stack.get(LysateCell.BATTERY_CELL_COMPONENT);

        if (batteryCheck != null && batteryCheck.cellin()) {

            if (batteryCheck.cellcharge() <= 0) return;
            if (energyCapability.getEnergyStored() == energyCapability.getMaxEnergyStored()) return;

            var avalibleCharge = Math.min(batteryCheck.cellcharge(), batteryCheck.cellsize() / 1200);
            var energySpace = energyCapability.getMaxEnergyStored() - energyCapability.getEnergyStored();

            var nextIn = Math.min(avalibleCharge, energySpace);
            energyCapability.receiveEnergy(nextIn, false);

            BatteryCellDataHandler update = new BatteryCellDataHandler(
                    true,
                    batteryCheck.celltype(),
                    batteryCheck.cellsize(),
                    batteryCheck.cellcharge() - nextIn
            );
            stack.set(LysateCell.BATTERY_CELL_COMPONENT, update);
        }
    }
}
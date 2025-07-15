package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;


public class BatteryCellLogic {

    public static void inventoryTick(ItemStack stack, LivingEntity entity, int slotId) {
        Level level = entity.level();


        // If it comes from the client, or if the entity is not a player,
        if (!(entity instanceof Player)) return;

        // If the game time is not divisible by 20 with no remainder, stop
//        if (level.getGameTime() % 20 != 0) return;

        // Check if it has battery_cell data components, doesn't have a battery in, or if the battery is empty
        BatteryCellDataHandler battery_cell = stack.get(LysateCell.BATTERY_CELL_COMPONENT);
        if (battery_cell == null || !battery_cell.cellin() || battery_cell.cellcharge() <= 0) return;

        /*
         Gets the smaller of the numbers between the remaining charge and the battery cell size divided by 120, as to make it take
         two minutes to fully drain if the item can always take the energy
        */
        int amountAvailable = Math.min(battery_cell.cellcharge(), battery_cell.cellsize() / 120);
        if (amountAvailable <= 0) return;

        // Check if this dang thing takes energy
        var object = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (object != null) {

            // Get storage capacity
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

            // Get 1/120 of cell size and give it to the item
            assert energyStorage != null;
            int energyin = energyStorage.receiveEnergy(battery_cell.cellsize() / 120, false);

            // Update the charge with the amount of energy used
            if (energyin > 0) {
                BatteryCellDataHandler updated = new BatteryCellDataHandler(
                        true,
                        battery_cell.celltype(),
                        battery_cell.cellsize(),
                        battery_cell.cellcharge() - energyin

                        );
                stack.set(LysateCell.BATTERY_CELL_COMPONENT, updated);
            }
        }
    }
}

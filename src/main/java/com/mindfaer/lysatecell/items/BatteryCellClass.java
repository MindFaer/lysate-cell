package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BatteryCellClass {


    public static void inventoryTick(ItemStack itemStack, LivingEntity entity, OmniSlot slot) {

        // Is if comes from the client, or if the entity is not a player,
        if (!(entity instanceof Player)) return;

        // If the game time is not divisable by 20 with no remainder, stop
        if (entity.level().getGameTime() % 20 != 0) return;

        // Check if it has battery_cell data components, doesn't have a battery in, or if the battery is empty
        BatteryCellDataHandler battery_cell = itemStack.get(LysateCell.BATTERY_CELL_COMPONENT);
        if (battery_cell == null || battery_cell.cellin() != 1 || battery_cell.cellcharge() <= 0) return;

        /*
         Gets the smaller of the numbers between the remaining charge and the battery cell size divided by 120, as to make it take
         two minutes to fully drain if the item can always take the energy
        */
        int amountAvailable = Math.min(battery_cell.cellcharge(), battery_cell.cellsize() / 120);
        if (amountAvailable <= 0) return;

        // Check if this dang thing takes energy
        var object = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (object != null) {

            // Get storage capacity
            IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);

            // Get 1/120 of cell size and give it to the item
            int energyin = energyStorage.receiveEnergy((battery_cell.cellsize()) / 120, false);

            // Update the charge with the amount of energy used
            if (energyin > 0) {
                BatteryCellDataHandler updated = new BatteryCellDataHandler(
                        battery_cell.cellin(),
                        battery_cell.cellcharge() - energyin,
                        battery_cell.celltype(),
                        battery_cell.cellsize()
                );
                itemStack.set(LysateCell.BATTERY_CELL_COMPONENT, updated);
            }
        }
    }
}

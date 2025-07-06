package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class BatteryCellClass {

    public static void inventoryTick(ItemStack itemStack, LivingEntity entity, OmniSlot slot) {
        BatteryCellDataHandler batteryCellDataHandler = itemStack.get(LysateCell.BATTERY_CELL_COMPONENT);
        assert batteryCellDataHandler != null;

        batteryCellDataHandler = batteryCellDataHandler.withHeldByOwner(isOwner(batteryCellDataHandler, entity));
        itemStack.set(LysateCell.BATTERY_CELL_COMPONENT, batteryCellDataHandler);


        if (isOwner(batteryCellDataHandler, entity)) {
            IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);

            if (energyStorage != null) {
                energyStorage.receiveEnergy((batteryCellDataHandler.cellsize() / 100), false);
            }


        }
    }
}

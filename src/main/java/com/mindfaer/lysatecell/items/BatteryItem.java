package com.mindfaer.lysatecell.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import com.mindfaer.lysatecell.LysateCell;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BatteryItem extends Item {
    public BatteryItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder < ItemStack > use(Level level, Player player, @NotNull InteractionHand usedHand) {

        ItemStack offhand = player.getItemInHand(usedHand);
        ItemStack mainHand = player.getMainHandItem();
        var cap = mainHand.getCapability(Capabilities.EnergyStorage.ITEM);
        IEnergyStorage energyStorage = offhand.getCapability(Capabilities.EnergyStorage.ITEM);

        if (!level.isClientSide() && player.getOffhandItem() == offhand && !mainHand.has(LysateCell.BATTERY_CELL_COMPONENT) && cap != null) {

            var batteryCheck = offhand.get(LysateCell.BATTERY_CELL_COMPONENT);

            if (batteryCheck != null) {

                level.playSound(null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.COPPER_TRAPDOOR_CLOSE,
                        SoundSource.PLAYERS,
                        0.75f,
                        1.5f
                );

                mainHand.set(LysateCell.BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(
                        true,
                        batteryCheck.celltype(),
                        energyStorage.getMaxEnergyStored(),
                        energyStorage.getEnergyStored()
                ));

                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

                return InteractionResultHolder.consume(offhand);
            }
        }
        return InteractionResultHolder.pass(offhand);
    }

    private String formatEnergy(int energy) {
        if (energy >= 1000000) {
            return String.format("%.1fM", energy / 1_000_000.0);
        } else if (energy >= 10000) {
            return String.format("%.1fk", energy / 1000.0);
        } else {
            return String.valueOf(energy);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        var component = stack.get(LysateCell.BATTERY_CELL_COMPONENT);
        int type = component.celltype();

        return switch (type) {
            case 1 -> 0xFF7007; // Oritech Orange
            case 2 -> 0x49CE00; // Green
            case 3 -> 0x4670A9; // Blue
            case 4 -> 0x863ACD; // Purple
            default -> 0xFF0000; // White, Fallback
        };
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return Math
                .round(energyStorage.getEnergyStored() * 100f / energyStorage.getMaxEnergyStored() * 13) / 100;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List < Component > tooltipComponents, TooltipFlag tooltipFlag) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (energyStorage != null) {
            int maxStorage = energyStorage.getMaxEnergyStored();
            int currentStorage = energyStorage.getEnergyStored();

            String formattedCurrent = formatEnergy(currentStorage);
            String formattedMax = formatEnergy(maxStorage);

            tooltipComponents.add(
                    Component
                            .translatable("tooltip.lysatecell.energy", formattedCurrent, formattedMax)
                            .withStyle(ChatFormatting.GOLD)
            );

        }

        var showExtra = Screen.hasShiftDown();

        if (showExtra) {
            tooltipComponents.add(
                    Component
                            .translatable("tooltip.lysatecell.extra_info_expanded")
                            .withStyle(ChatFormatting.GRAY)
            );
        } else {
            tooltipComponents.add(
                    Component
                            .translatable("tooltip.lysatecell.extra_info")
                            .withStyle(ChatFormatting.DARK_GRAY)
            );
        }
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

}
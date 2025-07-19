package com.mindfaer.lysatecell.items;

import net.minecraft.ChatFormatting;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import com.mindfaer.lysatecell.LysateCell;
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
        BatteryCellDataHandler cellData = offhand.get(LysateCell.BATTERY_CELL_COMPONENT);
        IEnergyStorage energyStorage = offhand.getCapability(Capabilities.EnergyStorage.ITEM);

        if (!level.isClientSide() && player.getOffhandItem() == offhand && !mainHand.has(LysateCell.BATTERY_CELL_COMPONENT) && cap != null) {

            level.playSound(null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.COPPER_TRAPDOOR_CLOSE,
                    SoundSource.PLAYERS,
                    0.75f,
                    1.5f
            );

            if (cellData != null) {

                boolean in = true;
                int charge = energyStorage.getEnergyStored();
                int type = cellData.celltype();
                int size = energyStorage.getMaxEnergyStored();

                mainHand.set(LysateCell.BATTERY_CELL_COMPONENT, cellData);

                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

                return InteractionResultHolder.consume(offhand);
            }
        }
        return InteractionResultHolder.pass(offhand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List < Component > tooltipComponents, TooltipFlag tooltipFlag) {

        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);

        if (energyStorage != null) {
            var maxStorage = energyStorage.getMaxEnergyStored();
            var currentStorage = energyStorage.getEnergyStored();

            tooltipComponents.add(Component.translatable("tooltip.lysatecell.energy", currentStorage, maxStorage).withStyle(ChatFormatting.GOLD));
        }
    }
}
package com.mindfaer.lysatecell.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import com.mindfaer.lysatecell.LysateCell;
import org.jetbrains.annotations.NotNull;


public class BatteryItem extends Item {
    public BatteryItem(Properties properties) {
        super(properties);
    }


    @Override
    public @NotNull InteractionResultHolder < ItemStack > use(Level level, Player player, @NotNull InteractionHand usedHand) {

        ItemStack offhand = player.getItemInHand(usedHand);
        ItemStack mainHand = player.getMainHandItem();

        if (!level.isClientSide() && player.getOffhandItem() == offhand) {

            var object2 = mainHand.getCapability(Capabilities.EnergyStorage.ITEM);
            if (!mainHand.has(LysateCell.BATTERY_CELL_COMPONENT) && object2 != null) {

                BatteryCellDataHandler cellData = offhand.get(LysateCell.BATTERY_CELL_COMPONENT);

                if (cellData != null) {

                    boolean in = true;
                    int charge = cellData.cellcharge();
                    int type = cellData.celltype();
                    int size = cellData.cellsize();

                    mainHand.set(LysateCell.BATTERY_CELL_COMPONENT, cellData);

                    player.playSound(SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON);

                    player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

                    return InteractionResultHolder.success(offhand);
                }
            }
        }

        return InteractionResultHolder.pass(offhand);
    }
}
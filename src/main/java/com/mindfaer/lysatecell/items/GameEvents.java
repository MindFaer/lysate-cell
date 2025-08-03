package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.client.KeyMapping;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = LysateCell.MODID, bus = EventBusSubscriber.Bus.GAME) public class GameEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            ServerUtilities.forBatteryInItems(living, BatteryCellLogic::inventoryTick);
        }
    }



    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem onUse) {
        ItemStack stack = onUse.getItemStack();
        Player player = onUse.getEntity();

        if (stack.get(LysateCell.BATTERY_CELL_COMPONENT) == null) return;
        var cap = stack.get(LysateCell.BATTERY_CELL_COMPONENT);

        if (cap == null) return;
        if (!cap.cellin()) return;

        if (!player.isShiftKeyDown()) return;
        {

            Item newItemType;
            switch (cap.celltype()) {
                case 1 -> newItemType = LysateCell.BASIC_BATTERY_CELL.get();
                case 2 -> newItemType = LysateCell.ADVANCED_BATTERY_CELL.get();
                case 3 -> newItemType = LysateCell.EXTREME_BATTERY_CELL.get();
                case 4 -> newItemType = LysateCell.LYSATE_BATTERY_CELL.get();
                default -> {
                    return;
                }
            }

            ItemStack newItem = new ItemStack(newItemType);

            BatteryCellDataHandler update = new BatteryCellDataHandler(
                    false,
                    cap.celltype(),
                    cap.cellsize(),
                    cap.cellcharge()
            );

            newItem.set(LysateCell.BATTERY_CELL_COMPONENT, update);
            var itemEnergy = newItem.getCapability(Capabilities.EnergyStorage.ITEM);

            if (itemEnergy != null) {
                newItem.set(LysateCell.LYSATE_ENERGY, cap.cellcharge());
            }

            stack.remove(LysateCell.BATTERY_CELL_COMPONENT);

            if (!player.getInventory().add(newItem)) {
                player.drop(newItem, false);
            }

            player.level().playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.COPPER_TRAPDOOR_OPEN,
                    SoundSource.PLAYERS,
                    0.75f,
                    1.5f
            );

        }
    }
}
package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ActivateBatteryPacket() implements CustomPacketPayload {

    public static final Type < ActivateBatteryPacket > TYPE =
            new Type < > (ResourceLocation.fromNamespaceAndPath(LysateCell.MODID, "activate_battery"));

    public static final StreamCodec < RegistryFriendlyByteBuf, ActivateBatteryPacket > STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {},
                    buf -> new ActivateBatteryPacket()
            );

    @Override
    public @NotNull Type < ? extends CustomPacketPayload > type() {
        return TYPE;
    }

    public static void handle(ActivateBatteryPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
                    Player player = context.player();
                    Level level = player.level();

                    //Battery In

                    if (!level.isClientSide()) {
                        ItemStack offhand = player.getOffhandItem();
                        ItemStack mainHand = player.getMainHandItem();
                        var cap = mainHand.getCapability(Capabilities.EnergyStorage.ITEM);
                        IEnergyStorage energyStorage = offhand.getCapability(Capabilities.EnergyStorage.ITEM);

                        if (!mainHand.has(LysateCell.BATTERY_CELL_COMPONENT) && cap != null) {
                            var batteryCheck = offhand.get(LysateCell.BATTERY_CELL_COMPONENT);

                            if (batteryCheck != null) {
                                level.playSound(
                                        null,
                                        player.getX(),
                                        player.getY(),
                                        player.getZ(),
                                        SoundEvents.COPPER_TRAPDOOR_CLOSE,
                                        SoundSource.PLAYERS,
                                        0.75f,
                                        1.5f
                                );

                                assert energyStorage != null;
                                mainHand.set(LysateCell.BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(
                                        true,
                                        batteryCheck.celltype(),
                                        energyStorage.getMaxEnergyStored(),
                                        energyStorage.getEnergyStored()
                                ));

                                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);


                            }




                        }

                        //Battery Out

                        else if (mainHand.has(LysateCell.BATTERY_CELL_COMPONENT)) {
                            var cellData = mainHand.get(LysateCell.BATTERY_CELL_COMPONENT);

                            if (cellData != null && cellData.cellin()) {
                                ItemStack stack = player.getMainHandItem();
                                Player player2 = context.player();

                                if (stack.get(LysateCell.BATTERY_CELL_COMPONENT) == null) return;
                                var cap2 = stack.get(LysateCell.BATTERY_CELL_COMPONENT);

                                if (cap2 == null) return;
                                if (!cap2.cellin()) return;

                                {
                                    Item newItemType;
                                    switch (cap2.celltype()) {
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
                                            cap2.celltype(),
                                            cap2.cellsize(),
                                            cap2.cellcharge()
                                    );

                                    newItem.set(LysateCell.BATTERY_CELL_COMPONENT, update);
                                    var itemEnergy = newItem.getCapability(Capabilities.EnergyStorage.ITEM);

                                    if (itemEnergy != null) {
                                        newItem.set(LysateCell.LYSATE_ENERGY, cap2.cellcharge());
                                    }

                                    stack.remove(LysateCell.BATTERY_CELL_COMPONENT);

                                    if (!player2.getInventory().add(newItem)) {
                                        player2.drop(newItem, false);
                                    }

                                    player2.level().playSound(null,
                                            player2.getX(), player2.getY(), player2.getZ(),
                                            SoundEvents.COPPER_TRAPDOOR_OPEN,
                                            SoundSource.PLAYERS,
                                            0.75f,
                                            1.5f
                                    );

                                }

                            }
                        }
                    }
                }
        );
    }
}
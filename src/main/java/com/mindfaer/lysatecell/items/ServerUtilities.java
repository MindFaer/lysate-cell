package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.util.TriConsumer;


@EventBusSubscriber(modid = LysateCell.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ServerUtilities {

    public static void forBatteryInItems(LivingEntity entity, TriConsumer < ItemStack, LivingEntity, Integer > consumer) {
        IItemHandler capability = entity.getCapability(Capabilities.ItemHandler.ENTITY);

        if (capability != null) {

            for (int i = 0; i < capability.getSlots(); i++) {
                ItemStack stack = capability.getStackInSlot(i);

                if (!stack.isEmpty() && stack.has(LysateCell.BATTERY_CELL_COMPONENT)) {
                    consumer.accept(stack, entity, i);
                }
            }
        }
    }
    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(LysateCell.MODID);

        registrar.playToServer(
                ActivateBatteryPacket.TYPE,
                ActivateBatteryPacket.STREAM_CODEC,
                ActivateBatteryPacket::handle
        );
    }

    public static void sendToServer(CustomPacketPayload payload) {
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(payload);
        }
    }

}
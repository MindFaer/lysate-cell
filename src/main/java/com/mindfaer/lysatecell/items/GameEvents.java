package com.mindfaer.lysatecell.items;


import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber(modid = LysateCell.MODID, bus = EventBusSubscriber.Bus.GAME)

public class GameEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            ServerUtilities.forBatteryInItems(living, BatteryCell::inventoryTick);
        }
    }
}

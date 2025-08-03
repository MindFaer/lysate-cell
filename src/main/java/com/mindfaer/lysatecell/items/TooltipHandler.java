package com.mindfaer.lysatecell.items;

import com.mindfaer.lysatecell.LysateCell;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.has(LysateCell.BATTERY_CELL_COMPONENT)) return;

        BatteryCellDataHandler battery = stack.get(LysateCell.BATTERY_CELL_COMPONENT);
        if (battery != null && battery.cellin()) {
            int charge = battery.cellcharge();
            int max = battery.cellsize();
            int type = battery.celltype();

            Component name =
                    switch (type) {
                        case 1 -> Component.translatable("tooltip.lysatecell.cell.cell1");
                        case 2 -> Component.translatable("tooltip.lysatecell.cell.cell2");
                        case 3 -> Component.translatable("tooltip.lysatecell.cell.cell3");
                        case 4 -> Component.translatable("tooltip.lysatecell.cell.cell4");
                        default -> Component.translatable("tooltip.lysatecell.cell.unknown");
                    };

            ChatFormatting color =
                    switch (type) {
                        case 1 -> ChatFormatting.GOLD; // Basic
                        case 2 -> ChatFormatting.GREEN; // Advanced
                        case 3 -> ChatFormatting.BLUE; // Extreme
                        case 4 -> ChatFormatting.LIGHT_PURPLE; // Lysate
                        default -> ChatFormatting.WHITE; //Backup
                    };

            String chargeStr = formatEnergy(charge);
            String sizeStr = formatEnergy(max);

            event.getToolTip().add(Component.translatable(
                    "tooltip.lysatecell.energytooltip",
                    chargeStr,
                    sizeStr
            ).withStyle(ChatFormatting.GOLD));

            event.getToolTip().add(Component.translatable(
                    "tooltip.lysatecell.tier",
                    name
            ).withStyle(color));

        }
    }

    private static String formatEnergy(int value) {
        if (value >= 1_000_000) return (value / 1_000_000) + "M";
        if (value >= 1_000) return (value / 1_000) + "k";
        return Integer.toString(value);
    }
}
package com.mindfaer.lysatecell;

import ca.weblite.objc.Client;
import com.google.common.graph.Network;
import com.mindfaer.lysatecell.items.ActivateBatteryPacket;
import com.mindfaer.lysatecell.items.BatteryCellDataHandler;
import com.mindfaer.lysatecell.items.BatteryItem;
import com.mindfaer.lysatecell.items.ServerUtilities;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(LysateCell.MODID)
public class LysateCell {
    public static final String MODID = "lysatecell";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredRegister < CreativeModeTab > CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    //Data component
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    // Battery Cell Data
    public static final DeferredHolder < DataComponentType < ? > , DataComponentType < BatteryCellDataHandler >> BATTERY_CELL_COMPONENT = DATA_COMPONENTS.registerComponentType("battery_cell",
            (builder) -> builder.persistent(BatteryCellDataHandler.CODEC).networkSynchronized(BatteryCellDataHandler.STREAM_CODEC));

    // Energy Component
    public static final DeferredHolder < DataComponentType < ? > , DataComponentType < Integer >> LYSATE_ENERGY = DATA_COMPONENTS.registerComponentType("lysate_energy",
            (builder) -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    //Items and blocks
    public static final DeferredItem < Item > BASIC_BATTERY_CELL = ITEMS.registerItem(
            "basic_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 1, 10000, 0))
            )
    );

    public static final DeferredItem < Item > ADVANCED_BATTERY_CELL = ITEMS.registerItem(
            "advanced_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 2, 100000, 0))
            )
    );

    public static final DeferredItem < Item > EXTREME_BATTERY_CELL = ITEMS.registerItem(
            "extreme_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 3, 1000000, 0))
            )
    );

    public static final DeferredItem < Item > LYSATE_BATTERY_CELL = ITEMS.registerItem(
            "lysate_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .fireResistant()
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 4, 5000000, 0))
            )
    );

    public static final DeferredItem < Item > DUMMY_ITEM = ITEMS.registerItem(
            "dummy_item",
            registryName -> new Item(
                    new Item.Properties()
                            .stacksTo(1)
            )
    );

    public static final DeferredItem < Item > BATTERY_PARTS = ITEMS.registerItem(
            "battery_parts",
            registryName -> new Item(
                    new Item.Properties()
            )
    );

    //Creative Tab
    public static final DeferredHolder < CreativeModeTab, CreativeModeTab > LYSATE_CELL_TAB = CREATIVE_MODE_TABS.register("lysate_cell_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lysatecell"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> DUMMY_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(BATTERY_PARTS.get());
                output.accept(BASIC_BATTERY_CELL.get());
                output.accept(ADVANCED_BATTERY_CELL.get());
                output.accept(EXTREME_BATTERY_CELL.get());
                output.accept(LYSATE_BATTERY_CELL.get());
                //                output.accept(DUMMY_ITEM.get());
            }).build());

    public LysateCell(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);

        ITEMS.register(modEventBus);

        DATA_COMPONENTS.register(modEventBus);

        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public class ClientModEvents {


        public static final Lazy<KeyMapping> BATTERY_KEY = Lazy.of(() -> new KeyMapping(
                "key.lysatecell.batterykey", // Will be localized using this translation key
                KeyConflictContext.UNIVERSAL,
                KeyModifier.SHIFT, // Default mapping requires shift to be held down
                InputConstants.Type.KEYSYM, // Default mapping is on the keyboard
                GLFW.GLFW_KEY_R, // Default key is R
                "key.categories.misc"
        ));

        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(BATTERY_KEY.get());
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
        while (BATTERY_KEY.get().consumeClick()) {
            ServerUtilities.sendToServer(new ActivateBatteryPacket());
            }
        }

        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.registerItem(
                    Capabilities.EnergyStorage.ITEM,

                    (itemStack, context) ->

                            new ComponentEnergyStorage(
                                    itemStack,
                                    LysateCell.LYSATE_ENERGY.get(),
                                    10000, 1000, 1000),

                    BASIC_BATTERY_CELL,
                    DUMMY_ITEM
            );
            event.registerItem(
                    Capabilities.EnergyStorage.ITEM,

                    (itemStack, context) ->

                            new ComponentEnergyStorage(
                                    itemStack,
                                    LysateCell.LYSATE_ENERGY.get(),
                                    100000, 10000, 10000),

                    ADVANCED_BATTERY_CELL
            );
            event.registerItem(
                    Capabilities.EnergyStorage.ITEM,

                    (itemStack, context) ->

                            new ComponentEnergyStorage(
                                    itemStack,
                                    LysateCell.LYSATE_ENERGY.get(),
                                    1000000, 100000, 100000),

                    EXTREME_BATTERY_CELL
            );
            event.registerItem(
                    Capabilities.EnergyStorage.ITEM,

                    (itemStack, context) ->

                            new ComponentEnergyStorage(
                                    itemStack,
                                    LysateCell.LYSATE_ENERGY.get(),
                                    5000000, 500000, 500000),

                    LYSATE_BATTERY_CELL
            );
        }
    }
}
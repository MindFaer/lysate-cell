package com.mindfaer.lysatecell;

import com.mindfaer.lysatecell.items.BatteryCellDataHandler;
import com.mindfaer.lysatecell.items.BatteryItem;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
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

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    //Data component

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    // Battery Cell Data
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BatteryCellDataHandler>> BATTERY_CELL_COMPONENT = DATA_COMPONENTS.registerComponentType("battery_cell",
            (builder) -> builder.persistent(BatteryCellDataHandler.CODEC).networkSynchronized(BatteryCellDataHandler.STREAM_CODEC));

    // Energy Component
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LYSATE_ENERGY = DATA_COMPONENTS.registerComponentType("lysate_energy",
            (builder) -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));


    //Items and blocks

    public static final DeferredBlock<Block> LYSATE_CELL_BLOCK = BLOCKS.registerSimpleBlock("lysate_cell_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));

    public static final DeferredItem<BlockItem> LYSATE_CELL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("lysate_cell_block_item", LYSATE_CELL_BLOCK);

    public static final DeferredItem<Item> BASIC_BATTERY_CELL = ITEMS.registerItem(
            "basic_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 1, 10000, 0))
            )
    );

    public static final DeferredItem<Item> ADVANCED_BATTERY_CELL = ITEMS.registerItem(
            "advanced_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 2, 100000, 0))
            )
    );

    public static final DeferredItem<Item> EXTREME_BATTERY_CELL = ITEMS.registerItem(
            "extreme_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 3, 1000000, 0))
            )
    );

    public static final DeferredItem<Item> LYSATE_BATTERY_CELL = ITEMS.registerItem(
            "lysate_battery_cell",
            registryName -> new BatteryItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .component(BATTERY_CELL_COMPONENT, new BatteryCellDataHandler(false, 4, 5000000, 0))
            )
    );

    public static final DeferredItem<Item> DUMMY_ITEM = ITEMS.registerItem(
            "dummy_item",
            registryName -> new Item(
                    new Item.Properties()
                            .stacksTo(1)
            )
    );




    //Creative Tab

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LYSATE_CELL_TAB = CREATIVE_MODE_TABS.register("lysate_cell_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.lysatecell"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> LYSATE_BATTERY_CELL.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(BASIC_BATTERY_CELL.get());
                output.accept(ADVANCED_BATTERY_CELL.get());
                output.accept(EXTREME_BATTERY_CELL.get());
                output.accept(LYSATE_BATTERY_CELL.get());
                output.accept(DUMMY_ITEM.get());
                output.accept(LYSATE_CELL_BLOCK_ITEM.get());
            }).build());


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public LysateCell(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
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

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    public class ClientModEvents {

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

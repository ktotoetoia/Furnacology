package com.im;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.databases.loot.LootDatabase;
import com.im.databases.registries.BlockRegistry;
import com.im.infrastructure.config.FurnacologyClientConfig;
import com.im.infrastructure.config.FurnacologyConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;

@Mod(Furnacology.ModId)
public final class FurnacologyNeoforge
{
    public FurnacologyNeoforge(IEventBus modEventBus, ModContainer container)
    {
        container.registerConfig(ModConfig.Type.COMMON, FurnacologyConfig.Spec);
        container.registerConfig(ModConfig.Type.CLIENT, FurnacologyClientConfig.Spec);

        Furnacology.init();

        modEventBus.addListener(FurnacologyNeoforge::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(FurnacologyNeoforge::injectLoot);
    }

    private static void injectLoot(LootTableLoadEvent event) { LootDatabase.Injector.getPoolsFor(event.getKey()).forEach(pool -> event.getTable().addPool(pool.build())); }

    private static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        BlockRegistry.ComponentEntities.forEach(type -> event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type.get(), FurnacologyNeoforge::getItems));
    }

    @Nullable
    private static IItemHandler getItems(ComponentBlockEntity entity, @Nullable Direction side)
    {
        WorldlyContainer access = entity.getAccess();

        if (access == null) return null;

        return side == null ? new InvWrapper(access) : new SidedInvWrapper(access, side);
    }
}
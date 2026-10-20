package com.im;

import com.im.infrastructure.platform.IMenuFactory;
import com.im.infrastructure.platform.IPlatformHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class FabricPlatformHelper implements IPlatformHelper
{
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(IMenuFactory<T> factory) { return new ExtendedScreenHandlerType<>(factory::create, BlockPos.STREAM_CODEC); }
    public CreativeModeTab.Builder createCreativeTab() { return FabricItemGroup.builder(); }

    public <T, TValue extends T> Supplier<TValue> register(Registry<T> registry, ResourceLocation id, Supplier<TValue> factory)
    {
        TValue value = Registry.register(registry, id, factory.get());
        return () -> value;
    }

    public void openMenu(ServerPlayer player, MenuProvider provider, BlockPos pos)
    {
        player.openMenu(new ExtendedScreenHandlerFactory<BlockPos>()
        {
            public BlockPos getScreenOpeningData(ServerPlayer opener) { return pos; }
            public Component getDisplayName() { return provider.getDisplayName(); }
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player opener) { return provider.createMenu(containerId, inventory, opener); }
        });
    }

    public int getBurnTime(ItemStack stack)
    {
        Integer time = FuelRegistry.INSTANCE.get(stack.getItem());
        return time == null ? 0 : time;
    }
}
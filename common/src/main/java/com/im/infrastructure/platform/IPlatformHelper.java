package com.im.infrastructure.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface IPlatformHelper
{
    <T, TValue extends T> Supplier<TValue> register(Registry<T> registry, ResourceLocation id, Supplier<TValue> factory);
    <T extends AbstractContainerMenu> MenuType<T> createMenuType(IMenuFactory<T> factory);
    void openMenu(ServerPlayer player, MenuProvider provider, BlockPos pos);
    int getBurnTime(ItemStack stack);
    CreativeModeTab.Builder createCreativeTab();
}

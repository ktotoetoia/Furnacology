package com.im.infrastructure.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IMenuFactory<T extends AbstractContainerMenu>
{
    T create(int containerId, Inventory inventory, BlockPos pos);
}
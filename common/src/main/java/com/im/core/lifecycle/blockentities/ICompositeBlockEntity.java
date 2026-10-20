package com.im.core.lifecycle.blockentities;

import com.im.core.lifecycle.typeregistry.ITypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface ICompositeBlockEntity extends ITypeRegistry<Object>
{
    @Nullable Level getLevel();
    BlockPos getBlockPos();
    Direction getFacing();
    boolean isServer();
    boolean isRemoved();
    void requestSync();
    void setChanged();
    void dropItem(ItemStack stack);
}
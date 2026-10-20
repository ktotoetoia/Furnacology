package com.im.infrastructure.saving;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public interface IItemDataSerializable
{
    void collectImplicit(DataComponentMap.Builder components);
    void applyImplicit(IDataSource source);
    default void removeFromTag(CompoundTag tag) { }

    interface IDataSource
    {
        @Nullable <T> T get(DataComponentType<T> type);
    }
}

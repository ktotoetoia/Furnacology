package com.im.infrastructure.saving;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IStateSerializable
{
    void captureState(CompoundTag tag, HolderLookup.Provider registries);
    void restoreState(CompoundTag tag, HolderLookup.Provider registries);
}

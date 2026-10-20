package com.im.infrastructure.saving;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface IPersistentSerializable extends IStateSerializable, ISyncSerializable
{
    void capture(CompoundTag tag);
    void restore(CompoundTag tag);

    default void captureState(CompoundTag tag, HolderLookup.Provider registries) { capture(tag); }
    default void restoreState(CompoundTag tag, HolderLookup.Provider registries) { restore(tag); }
    default void captureSync(CompoundTag tag) { capture(tag); }
    default void restoreSync(CompoundTag tag) { restore(tag); }
}

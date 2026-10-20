package com.im.infrastructure.saving;

import net.minecraft.nbt.CompoundTag;

public interface ISyncSerializable
{
    void captureSync(CompoundTag tag);
    void restoreSync(CompoundTag tag);
}

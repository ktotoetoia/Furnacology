package com.im.core.lifecycle.blockentities;

import net.minecraft.server.level.ServerLevel;

public interface IRemovedObserver
{
    void onRemoved(ServerLevel level);
}
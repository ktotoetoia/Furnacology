package com.im.gameplay.skins;

import net.minecraft.resources.ResourceLocation;

public interface ISkinRequest
{
    ResourceLocation skin();
    int priority();
}
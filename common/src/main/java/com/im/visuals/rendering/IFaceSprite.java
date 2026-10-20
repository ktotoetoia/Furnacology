package com.im.visuals.rendering;

import net.minecraft.resources.ResourceLocation;

public interface IFaceSprite
{
    ResourceLocation getTexture();
    float getFrameOffset();
    float getFrameHeight();
    boolean isVisible();
}
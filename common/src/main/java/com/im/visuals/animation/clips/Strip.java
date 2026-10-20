package com.im.visuals.animation.clips;

import net.minecraft.resources.ResourceLocation;

public record Strip(ResourceLocation texture, int frameCount)
{
    public Strip
    {
        frameCount = Math.max(1, frameCount);
    }
}
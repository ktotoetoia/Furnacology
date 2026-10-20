package com.im.ui;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public record GuiSprite(ResourceLocation texture, int width, int height, @Nullable GuiSprite fallback) { }

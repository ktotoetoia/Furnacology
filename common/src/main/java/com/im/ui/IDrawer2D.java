package com.im.ui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface IDrawer2D
{
    void drawSprite(GuiSprite sprite, int x, int y, int u, int v, int width, int height);
    void drawAtlasSprite(ResourceLocation sprite, int x, int y, int size);
    void drawItem(ItemStack stack, int x, int y, float scale);
    void drawText(Component text, int x, int y, int color, boolean shadow);
    int getTextWidth(Component text);

    default void drawSprite(GuiSprite sprite, int x, int y) { drawSprite(sprite, x, y, 0, 0, sprite.width(), sprite.height()); }
}

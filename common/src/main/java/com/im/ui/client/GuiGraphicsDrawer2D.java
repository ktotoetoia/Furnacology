package com.im.ui.client;

import com.im.ui.GuiSprite;
import com.im.ui.IDrawer2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GuiGraphicsDrawer2D implements IDrawer2D
{
    private final Map<ResourceLocation, Boolean> _existing = new HashMap<>();
    private GuiGraphics _graphics;

    public GuiGraphicsDrawer2D begin(GuiGraphics graphics)
    {
        _graphics = graphics;
        return this;
    }

    public void drawSprite(GuiSprite sprite, int x, int y, int u, int v, int width, int height) { _graphics.blit(resolve(sprite), x, y, u, v, width, height, sprite.width(), sprite.height()); }
    public void drawAtlasSprite(ResourceLocation sprite, int x, int y, int size) { _graphics.blitSprite(sprite, x, y, size, size); }
    public void drawText(Component text, int x, int y, int color, boolean shadow) { _graphics.drawString(getFont(), text, x, y, color, shadow); }
    public int getTextWidth(Component text) { return getFont().width(text); }

    public void drawItem(ItemStack stack, int x, int y, float scale)
    {
        _graphics.pose().pushPose();
        _graphics.pose().translate(x, y, 0.0f);
        _graphics.pose().scale(scale, scale, 1.0f);
        _graphics.renderItem(stack, 0, 0);
        _graphics.pose().popPose();
    }

    private static Font getFont() { return Minecraft.getInstance().font; }
    private static boolean exists(ResourceLocation texture) { return Minecraft.getInstance().getResourceManager().getResource(texture).isPresent(); }

    private ResourceLocation resolve(GuiSprite sprite)
    {
        for (GuiSprite candidate = sprite; candidate != null; candidate = candidate.fallback())
        {
            if (_existing.computeIfAbsent(candidate.texture(), GuiGraphicsDrawer2D::exists)) return candidate.texture();
        }

        return sprite.texture();
    }
}

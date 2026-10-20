package com.im.ui;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class UIElement
{
    private static final int HoverMargin = 1;

    private final GuiArea _area;
    private final IContent _content;
    private BooleanSupplier _visible = () -> true;
    private Supplier<Component> _tooltip = () -> null;

    UIElement(GuiArea area, IContent content)
    {
        _area = area;
        _content = content;
    }

    public GuiArea getArea() { return _area; }
    public boolean isVisible() { return _visible.getAsBoolean(); }

    public UIElement withVisible(BooleanSupplier visible)
    {
        _visible = visible;
        return this;
    }

    public UIElement withTooltip(Supplier<Component> tooltip)
    {
        _tooltip = tooltip;
        return this;
    }

    void render(IDrawer2D painter, int left, int top)
    {
        if (isVisible()) _content.draw(painter, left + _area.x(), top + _area.y());
    }

    @Nullable
    Component getTooltip(double mouseX, double mouseY) { return isVisible() && _area.inflate(HoverMargin).contains(mouseX, mouseY) ? _tooltip.get() : null; }

    @FunctionalInterface
    public interface IContent
    {
        IContent None = (painter, x, y) -> { };

        void draw(IDrawer2D painter, int x, int y);
    }
}

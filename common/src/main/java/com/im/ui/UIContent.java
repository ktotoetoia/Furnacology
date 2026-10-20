package com.im.ui;

import com.im.core.values.cappedvalues.ICappedValueReadOnly;
import com.im.ui.UIElement.IContent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public final class UIContent
{
    private static final int TextColor = 0x404040;
    private static final int CountColor = 0xFFFFFF;
    private static final int IconSize = 12;
    private static final int IconRaise = 2;
    private static final int IconGap = 3;
    private static final float IconScale = IconSize / 16.0f;
    private static final int RowHeight = 18;
    private static final int ItemSize = 16;
    private static final int RowTextOffset = 4;
    private static final int RowCountOffset = 9;

    public static IContent image(Supplier<GuiSprite> sprite) { return (painter, x, y) -> painter.drawSprite(sprite.get(), x, y); }

    public static IContent label(boolean rightAligned, Supplier<Component> text, Supplier<ItemStack> icon)
    {
        return (painter, x, y) ->
        {
            Component value = text.get();
            ItemStack stack = icon.get();
            int left = rightAligned ? x - painter.getTextWidth(value) : x;

            painter.drawText(value, left, y, TextColor, false);

            if (!stack.isEmpty()) painter.drawItem(stack, left - IconGap - IconSize, y - IconRaise, IconScale);
        };
    }

    public static IContent fill(Supplier<GuiSprite> sprite, FillDirection direction, IFillScale scale, Supplier<ICappedValueReadOnly<Integer>> value)
    {
        return (painter, x, y) ->
        {
            GuiSprite current = sprite.get();
            boolean horizontal = direction.isHorizontal();
            int filled = getFilled(value.get(), scale, horizontal ? current.width() : current.height());

            if (filled <= 0) return;

            int u = direction == FillDirection.RightToLeft ? current.width() - filled : 0;
            int v = direction == FillDirection.BottomToTop ? current.height() - filled : 0;

            painter.drawSprite(current, x + u, y + v, u, v, horizontal ? filled : current.width(), horizontal ? current.height() : filled);
        };
    }

    public static IContent panel(Supplier<GuiSprite> sprite, int padding, Supplier<List<Row>> rows)
    {
        return (painter, x, y) ->
        {
            GuiSprite current = sprite.get();
            int bottom = y + current.height() - padding;
            int rowY = y + padding;

            painter.drawSprite(current, x, y);

            for (Row row : rows.get())
            {
                if (rowY + ItemSize > bottom) break;

                drawRow(painter, row, row.hasText() ? x + padding : x + (current.width() - ItemSize) / 2, rowY);
                rowY += RowHeight;
            }
        };
    }

    private static int getFilled(ICappedValueReadOnly<Integer> value, IFillScale scale, int length)
    {
        return value.value() <= 0 || value.maxValue() <= 0 ? 0 : Mth.clamp(scale.scale(value.value(), value.maxValue(), length), 0, length);
    }

    private static void drawRow(IDrawer2D painter, Row row, int x, int y)
    {
        if (!row.item().isEmpty()) painter.drawItem(row.item(), x, y, 1.0f);
        else if (row.sprite() != null) painter.drawAtlasSprite(row.sprite(), x, y, ItemSize);

        if (row.count() > 1)
        {
            Component count = Component.literal(String.valueOf(row.count()));
            painter.drawText(count, x + ItemSize + 1 - painter.getTextWidth(count), y + RowCountOffset, CountColor, true);
        }

        if (row.hasText()) painter.drawText(row.text(), x + ItemSize + 1, y + RowTextOffset, TextColor, false);
    }

    public enum FillDirection
    {
        LeftToRight, RightToLeft, BottomToTop, TopToBottom;

        public boolean isHorizontal() { return this == LeftToRight || this == RightToLeft; }
    }

    public interface IFillScale
    {
        IFillScale Linear = (value, max, length) -> (int) ((long) value * length / max);
        IFillScale CeilWithBase = (value, max, length) -> Mth.ceil((float) value * (length - 1) / max) + 1;

        int scale(int value, int max, int length);
    }

    public record Row(ItemStack item, @Nullable ResourceLocation sprite, Component text, int count)
    {
        public static Row item(ItemStack item, Component text) { return new Row(item, null, text, 0); }
        public static Row sprite(ResourceLocation sprite, int count) { return new Row(ItemStack.EMPTY, sprite, Component.empty(), count); }
        public boolean hasText() { return !text.getString().isEmpty(); }
    }

    private UIContent() { }
}

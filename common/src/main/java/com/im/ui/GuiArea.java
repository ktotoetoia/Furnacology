package com.im.ui;

public record GuiArea(int x, int y, int width, int height)
{
    public GuiArea inflate(int amount)
    {
        return new GuiArea(x - amount, y - amount, width + amount * 2, height + amount * 2);
    }

    public boolean contains(double pointX, double pointY) { return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height; }
}
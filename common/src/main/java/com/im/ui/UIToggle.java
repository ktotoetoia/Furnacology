package com.im.ui;

import net.minecraft.network.chat.Component;

public final class UIToggle
{
    private final GuiArea _area;
    private final Component _label;
    private boolean _open;

    UIToggle(GuiArea area, Component label)
    {
        _area = area;
        _label = label;
    }

    public GuiArea getArea() { return _area; }
    public Component getLabel() { return _label; }
    public boolean isOpen() { return _open; }
    public void flip() { _open = !_open; }
    public void close() { _open = false; }
}

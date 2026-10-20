package com.im.ui;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.core.menu.ComponentMenu;
import com.im.core.menu.MenuDataTracker;
import com.im.core.menu.MenuSlot;
import com.im.ui.UIElement.IContent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class UIDocument implements MenuProvider
{
    private final ComponentBlockEntity _entity;
    private final Component _title;
    private final Supplier<? extends MenuType<ComponentMenu>> _menuType;
    private final Supplier<GuiSprite> _background;
    private final Supplier<List<MenuSlot>> _slots;
    private final List<UIElement> _elements = new ArrayList<>();
    private final List<UIToggle> _toggles = new ArrayList<>();
    private final MenuDataTracker _data = new MenuDataTracker();

    public UIDocument(ComponentBlockEntity entity, Component title, Supplier<? extends MenuType<ComponentMenu>> menuType, Supplier<GuiSprite> background,
                      Supplier<List<MenuSlot>> slots)
    {
        _entity = entity;
        _title = title;
        _menuType = menuType;
        _background = background;
        _slots = slots;
    }

    public UIElement add(GuiArea area, IContent content)
    {
        UIElement element = new UIElement(area, content);
        _elements.add(element);
        return element;
    }

    public UIToggle addToggle(GuiArea area, Component label)
    {
        UIToggle toggle = new UIToggle(area, label);
        _toggles.add(toggle);
        return toggle;
    }

    public void render(IDrawer2D painter, int left, int top) { _elements.forEach(element -> element.render(painter, left, top)); }

    @Nullable
    public Component getTooltip(double mouseX, double mouseY)
    {
        for (UIElement element : _elements)
        {
            Component tooltip = element.getTooltip(mouseX, mouseY);

            if (tooltip != null) return tooltip;
        }

        return null;
    }

    public boolean isOverElement(double mouseX, double mouseY) { return _elements.stream().anyMatch(element -> element.isVisible() && element.getArea().contains(mouseX, mouseY)); }

    public List<UIToggle> getToggles() { return Collections.unmodifiableList(_toggles); }
    public MenuDataTracker getData() { return _data; }
    public GuiSprite getBackground() { return _background.get(); }
    public List<MenuSlot> getSlots() { return _slots.get(); }
    public Component getDisplayName() { return _title; }
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) { return new ComponentMenu(_menuType.get(), containerId, inventory, _entity); }
}

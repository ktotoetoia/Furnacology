package com.im.core.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record MenuSlot(Container items, int index, int x, int y, int priority, boolean takeOnly, Consumer<Player> onTake)
{
    private static final Consumer<Player> Nothing = player -> { };

    public static MenuSlot of(Container items, int index, int x, int y, int priority) { return new MenuSlot(items, index, x, y, priority, false, Nothing); }
    public static MenuSlot output(Container items, int index, int x, int y, Consumer<Player> onTake) { return new MenuSlot(items, index, x, y, 0, true, onTake); }
}
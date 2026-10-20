package com.im.gameplay.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ColoredNameItem extends Item
{
    private final ChatFormatting _color;

    public ColoredNameItem(Properties properties, ChatFormatting color)
    {
        super(properties);
        _color = color;
    }

    @Override
    public Component getName(ItemStack stack) { return super.getName(stack).copy().withStyle(_color); }
}
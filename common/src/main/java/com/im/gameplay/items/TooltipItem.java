package com.im.gameplay.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Function;

public class TooltipItem extends Item
{
    private final Function<ItemStack, List<Component>> _lines;

    public TooltipItem(Properties properties,Function<ItemStack,List<Component>> lines)
    {
        super(properties);
        _lines = lines;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) { tooltip.addAll(_lines.apply(stack)); }
}

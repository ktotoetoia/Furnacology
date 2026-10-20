package com.im.gameplay.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DescribedItem extends Item
{
    private final List<Component> _header;

    public DescribedItem(Properties properties, Component... header)
    {
        super(properties);
        _header = List.of(header);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.addAll(_header);
        ItemDescription.append(getDescriptionId(), tooltip);
    }
}

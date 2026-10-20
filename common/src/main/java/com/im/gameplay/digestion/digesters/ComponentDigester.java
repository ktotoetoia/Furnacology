package com.im.gameplay.digestion.digesters;

import com.im.gameplay.buffs.BuffGrant;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class ComponentDigester implements IDigester<ItemStack>
{
    private final Supplier<DataComponentType<List<BuffGrant>>> _component;

    public ComponentDigester(Supplier<DataComponentType<List<BuffGrant>>> component) { _component = component; }

    public List<BuffGrant> digest(ItemStack input) { return input.getOrDefault(_component.get(), List.of()); }
}

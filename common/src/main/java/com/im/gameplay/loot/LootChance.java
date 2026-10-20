package com.im.gameplay.loot;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

public record LootChance(Supplier<Double> base,ResourceKey<Enchantment> enchantment,Supplier<Double> bonusPerLevel)
{
    public double getChanceAt(int level)
    {
        return base.get() + level * bonusPerLevel.get();
    }
}
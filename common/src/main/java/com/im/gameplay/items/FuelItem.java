package com.im.gameplay.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FuelItem extends Item implements IFuelItem
{
    private final Supplier<Integer> _burnTime;

    public FuelItem(Properties properties, Supplier<Integer> burnTime)
    {
        super(properties);
        _burnTime = burnTime;
    }

    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) { return _burnTime.get(); }
}
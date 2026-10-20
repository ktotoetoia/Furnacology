package com.im.gameplay.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;

public interface IFuelItem
{
    int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType);
}
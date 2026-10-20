package com.im.gameplay.entityactions;

import com.im.gameplay.smelting.furnace.ExperienceStorage;
import com.im.gameplay.smelting.furnace.Furnace;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class SmeltAction implements IEntityAction
{
    private final Furnace _furnace;
    private final ExperienceStorage _experience;

    public SmeltAction(Furnace furnace, ExperienceStorage experience)
    {
        _furnace = furnace;
        _experience = experience;
    }

    @Override
    public void tick()
    {
        if (!_furnace.isServer()) return;

        if (_furnace.isLit()) _furnace.setBurnTime(_furnace.getBurnTime() - 1);

        RecipeHolder<SmeltingRecipe> recipe = _furnace.findRecipe();
        boolean cooking = recipe != null && (_furnace.isLit() || _furnace.burnFuel());

        if (recipe == null) _furnace.setProgress(0);
        else if (cooking) advance(recipe);
        else _furnace.setProgress(Math.max(0, _furnace.getProgress() - 1));

        _furnace.setCooking(cooking);
    }

    private void advance(RecipeHolder<SmeltingRecipe> recipe)
    {
        _furnace.setMaxProgress(Math.max(1, Math.round(recipe.value().getCookingTime() / _furnace.getCurrentSpeed())));
        _furnace.setProgress(_furnace.getProgress() + 1);

        if (_furnace.getProgress() < _furnace.getMaxProgress()) return;

        _furnace.setProgress(0);
        _furnace.smelt(recipe);
        _experience.record(recipe);
    }
}
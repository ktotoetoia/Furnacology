package com.im.gameplay.entityactions;

import com.im.gameplay.smelting.furnace.Furnace;

public class IgniteAction implements IEntityAction
{
    private final Furnace _furnace;

    public IgniteAction(Furnace furnace) { _furnace = furnace; }

    @Override
    public void tick()
    {
        if (_furnace.isServer() && !_furnace.isLit() && _furnace.hasRecipe()) _furnace.burnFuel();
    }
}
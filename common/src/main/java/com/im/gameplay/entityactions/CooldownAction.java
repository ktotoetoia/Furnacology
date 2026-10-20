package com.im.gameplay.entityactions;

import com.im.gameplay.smelting.furnace.Furnace;

public class CooldownAction implements IEntityAction
{
    private final Furnace _furnace;
    private final int _perTick;

    public CooldownAction(Furnace furnace, int perTick)
    {
        _furnace = furnace;
        _perTick = perTick;
    }

    @Override
    public void tick()
    {
        if (!_furnace.isServer() || _furnace.getProgress() <=  0) return;
        _furnace.setProgress(_furnace.hasRecipe() ? Math.max(0, _furnace.getProgress() - _perTick) : 0);
    }
}
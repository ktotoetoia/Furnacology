package com.im.gameplay.entityactions;

import com.im.gameplay.abilities.AbilityUser;
import com.im.gameplay.digestion.Digestion;
import com.im.gameplay.effects.containers.EffectContainer;
import com.im.gameplay.smelting.furnace.Furnace;

public class BuffAction implements IEntityAction
{
    private final Furnace _furnace;
    private final EffectContainer _effects;
    private final Digestion _digestion;
    private final AbilityUser _abilities;

    public BuffAction(Furnace furnace, EffectContainer effects, Digestion digestion, AbilityUser abilities)
    {
        _furnace = furnace;
        _effects = effects;
        _digestion = digestion;
        _abilities = abilities;
    }

    @Override
    public void tick()
    {
        if (!_furnace.isServer()) return;

        _abilities.updateQuietTicks(_furnace.isSmelting());
        _effects.update();
        _digestion.updateDigestion(_furnace.isSmelting());
    }
}

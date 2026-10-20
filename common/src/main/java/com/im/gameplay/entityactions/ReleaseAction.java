package com.im.gameplay.entityactions;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.abilities.AbilityRelease;
import com.im.gameplay.abilities.AbilityUser;

import java.util.ArrayList;
import java.util.List;

public class ReleaseAction implements IEntityAction
{
    private final ICompositeBlockEntity _entity;
    private final AbilityUser _abilities;
    private final List<AbilityRelease> _fired = new ArrayList<>();

    public ReleaseAction(ICompositeBlockEntity entity, AbilityUser abilities)
    {
        _entity = entity;
        _abilities = abilities;
    }

    @Override
    public void onEnter()
    {
        _abilities.resetReleaseTicks();

        if (!_entity.isServer()) return;

        for (AbilityRelease release : _abilities.release())
        {
            release.buff().instant().cast(_entity, release.degree());
            _fired.add(release);
        }
    }

    @Override
    public void tick()
    {
        int ticks = _abilities.nextReleaseTick();

        for (AbilityRelease release : _fired)
        {
            if (ticks < release.duration()) release.buff().instant().tick(_entity, release.degree(), (float) ticks / release.duration());
        }
    }

    @Override
    public void onExit() { _fired.clear(); }
}
package com.im.gameplay.effects.groups;

import com.im.core.statemachine.statemachine.ITickable;
import com.im.gameplay.effects.modifiers.IEffectModifier;

import java.util.Collection;

public class TemporaryEffectGroup extends EffectGroup implements ITemporaryEffectGroup, IUpdatableEffectGroup
{
    private final int _duration;
    private int _remaining;

    public TemporaryEffectGroup(Collection<? extends IEffectModifier> modifiers, int duration, int remaining)
    {
        super(modifiers);
        _duration = duration;
        _remaining = remaining;
    }

    public int getDuration() { return _duration; }
    public int getRemaining() { return _remaining; }
    public boolean isFinished() { return _remaining <= 0; }

    public void update()
    {
        getModifiers().getAll(ITickable.class).forEach(ITickable::tick);
        _remaining--;
    }
}

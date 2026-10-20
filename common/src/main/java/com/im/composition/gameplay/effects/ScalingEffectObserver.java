package com.im.composition.gameplay.effects;

import com.im.core.values.scaling.IScaleModifier;
import com.im.core.values.scaling.IScalingValue;
import com.im.gameplay.effects.containers.IEffectObserver;
import com.im.gameplay.effects.groups.IEffectGroup;

import java.util.function.Function;

public class ScalingEffectObserver<T> implements IEffectObserver
{
    private final Class<T> _type;
    private final Function<T, IScaleModifier> _modifier;
    private final IScalingValue _value;

    public ScalingEffectObserver(Class<T> type, Function<T, IScaleModifier> modifier, IScalingValue value)
    {
        _type = type;
        _modifier = modifier;
        _value = value;
    }

    public void onEffectGroupAdded(IEffectGroup group) { group.getModifiers().getAll(_type).forEach(modifier -> _value.add(_modifier.apply(modifier))); }
    public void onEffectGroupRemoved(IEffectGroup group) { group.getModifiers().getAll(_type).forEach(modifier -> _value.remove(_modifier.apply(modifier))); }
}
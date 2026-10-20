package com.im.core.values.scaling;

public interface IModifierAggregator
{
    float combine(float rawValue, Iterable<? extends IScaleModifier> modifiers);
}
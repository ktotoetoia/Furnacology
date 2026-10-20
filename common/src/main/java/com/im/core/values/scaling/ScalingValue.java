package com.im.core.values.scaling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ScalingValue implements IScalingValue
{
    private final List<IScaleModifier> _modifiers = new ArrayList<>();
    private final Supplier<Float> _rawValue;
    private final IModifierAggregator _aggregator;

    public ScalingValue(Supplier<Float> rawValue) { this(rawValue, new DefaultAggregator()); }
    public ScalingValue(Supplier<Float> rawValue, IModifierAggregator aggregator)
    {
        _rawValue = rawValue;
        _aggregator = aggregator;
    }

    public float getRawValue() { return _rawValue.get(); }
    public float getFinalValue() { return scale(getRawValue()); }
    public float scale(float rawValue) { return _aggregator.combine(rawValue, _modifiers); }
    public List<IScaleModifier> getModifiers() { return Collections.unmodifiableList(_modifiers); }
    public boolean add(IScaleModifier modifier) { return _modifiers.add(modifier); }
    public boolean remove(IScaleModifier modifier) { return _modifiers.remove(modifier); }
}
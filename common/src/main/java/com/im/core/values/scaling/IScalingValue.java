package com.im.core.values.scaling;

import java.util.List;

public interface IScalingValue
{
    float getRawValue();
    float getFinalValue();
    float scale(float rawValue);
    List<IScaleModifier> getModifiers();
    boolean add(IScaleModifier modifier);
    boolean remove(IScaleModifier modifier);
}
package com.im.core.values.scaling;

public class DefaultAggregator implements IModifierAggregator
{
    public float combine(float rawValue, Iterable<? extends IScaleModifier> modifiers)
    {
        float sum = rawValue;
        float percent = 1.0f;

        for (IScaleModifier modifier : modifiers)
        {
            sum += modifier.add();
            percent += modifier.multiplication() - 1.0f;
        }

        return sum * percent;
    }
}
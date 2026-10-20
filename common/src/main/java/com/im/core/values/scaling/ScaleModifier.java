package com.im.core.values.scaling;

public record ScaleModifier(float add, float multiplication) implements IScaleModifier
{
    public ScaleModifier(float add) { this(add, 1.0f); }
}
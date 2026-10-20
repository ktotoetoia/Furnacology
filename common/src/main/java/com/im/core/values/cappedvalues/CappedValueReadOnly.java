package com.im.core.values.cappedvalues;

public record CappedValueReadOnly<T>(T value, T minValue, T maxValue) implements ICappedValueReadOnly<T>
{
    public String toString() { return "Value: " + value + ". Min: " + minValue + ". Max: " + maxValue; }
}
package com.im.core.values.cappedvalues;

public interface ICappedValueReadOnly<T>
{
    T value();
    T minValue();
    T maxValue();
}
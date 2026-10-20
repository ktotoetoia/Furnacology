package com.im.core.values.random;

import java.util.function.Supplier;

public record WeightedEntry<T>(T item, Supplier<Double> weight)
{
    public double getChance()
    {
        return Math.max(0.0, weight.get());
    }
}
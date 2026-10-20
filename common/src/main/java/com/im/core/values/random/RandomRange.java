package com.im.core.values.random;

import net.minecraft.util.RandomSource;

import java.util.function.Supplier;

public record RandomRange(Supplier<Double> min, Supplier<Double> max)
{
    public double sample(RandomSource random)
    {
        double low = min.get();
        return low + random.nextDouble() * (max.get() - low);
    }
}
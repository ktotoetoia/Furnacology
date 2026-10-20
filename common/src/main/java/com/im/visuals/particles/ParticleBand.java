package com.im.visuals.particles;

import com.im.core.values.random.RandomRange;

import java.util.function.Supplier;

public record ParticleBand(RandomRange spawn, RandomRange speed, RandomRange rise, RandomRange lifetime, Supplier<Double> perTick) { }
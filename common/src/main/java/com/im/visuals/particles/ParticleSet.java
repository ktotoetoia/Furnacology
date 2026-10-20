package com.im.visuals.particles;

import net.minecraft.core.particles.ParticleOptions;

import java.util.List;
import java.util.function.IntFunction;

public record ParticleSet(List<IntFunction<? extends ParticleOptions>> sizes) { }

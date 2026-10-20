package com.im.visuals.particles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public record TimedParticleOptions(TimedParticleType type, int lifetime) implements ParticleOptions
{
    public ParticleType<?> getType() { return type; }
}
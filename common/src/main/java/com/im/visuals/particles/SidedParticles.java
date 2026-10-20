package com.im.visuals.particles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SidedParticles
{
    public static void emit(Level level, ParticleOptions particle, Vec3 position, Vec3 velocity)
    {
        if (level instanceof ServerLevel server) server.sendParticles(particle, position.x, position.y, position.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
        else level.addParticle(particle, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }
}
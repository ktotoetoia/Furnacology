package com.im.visuals.particles;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public record ParticleStreak(Supplier<Integer> count, Supplier<Integer> ticks, double spread)
{
    public void emit(Level level, ParticleSet set, Vec3 from, Vec3 to, RandomSource random)
    {
        List<IntFunction<? extends ParticleOptions>> sizes = set.sizes();

        if (sizes.isEmpty()) return;

        int lifetime = Math.max(1, ticks.get());
        Vec3 path = to.subtract(from);

        for (int i = count.get(); i > 0; i--)
        {
            double start = random.nextDouble() * spread;
            ParticleOptions particle = sizes.get(random.nextInt(sizes.size())).apply(lifetime);

            SidedParticles.emit(level, particle, from.add(path.scale(start)), path.scale((1.0 - start) / lifetime));
        }
    }
}
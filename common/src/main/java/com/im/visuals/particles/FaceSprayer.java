package com.im.visuals.particles;

import com.im.core.values.random.RandomRange;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.IntFunction;
import java.util.function.ToDoubleFunction;

public class FaceSprayer
{
    private static final double SurfaceOffset = 0.51;
    private static final double WallClearance = 0.16;

    private final RandomSource _random = RandomSource.create();

    public void spray(Level level, BlockPos pos, Direction face, RandomRange across, RandomRange height, ParticleBand band,
                      IntFunction<? extends ParticleOptions> particle, ToDoubleFunction<Vec3> reach)
    {
        Vec3 out = Vec3.atLowerCornerOf(face.getNormal());
        Vec3 right = Vec3.atLowerCornerOf(face.getCounterClockWise().getNormal());
        Vec3 center = Vec3.atCenterOf(pos);

        for (int count = getCount(band.perTick().get()); count > 0; count--)
        {
            Vec3 mouth = center.add(right.scale(across.sample(_random) / 16.0 - 0.5)).add(0.0, height.sample(_random) / 16.0 - 0.5, 0.0);

            double room = reach.applyAsDouble(mouth) - WallClearance;
            double distance = Math.min(SurfaceOffset + Math.max(0.0, room) * band.spawn().sample(_random) / 100f, 0.5 + room);

            Vec3 velocity = out.scale(band.speed().sample(_random)).add(0.0, band.rise().sample(_random), 0.0);

            SidedParticles.emit(level, particle.apply((int) Math.round(band.lifetime().sample(_random))), mouth.add(out.scale(distance)), velocity);
        }
    }

    private int getCount(double perTick)
    {
        int whole = (int) perTick;
        return whole + (_random.nextDouble() < perTick - whole ? 1 : 0);
    }
}
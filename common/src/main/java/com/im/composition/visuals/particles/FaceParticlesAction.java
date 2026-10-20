package com.im.composition.visuals.particles;

import com.im.gameplay.entityactions.IEntityAction;
import com.im.visuals.particles.ParticleEmitter;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class FaceParticlesAction implements IEntityAction
{
    private static final double FaceOffset = 0.52;
    private static final double MinOutwardSpeed = 0.03;
    private static final double MaxOutwardSpeed = 0.08;
    private static final double MinUpwardSpeed = 0.04;
    private static final double MaxUpwardSpeed = 0.12;
    private static final double SidewaysSpeed = 0.04;

    private final ParticleEmitter _emitter;
    private final Supplier<Direction> _face;
    private final Supplier<ParticleOptions> _particle;
    private final BooleanSupplier _running;
    private final float _chance;
    private final double _minHeight;
    private final double _maxHeight;
    private final double _width;
    private final RandomSource _randomSource = RandomSource.create();

    public FaceParticlesAction(ParticleEmitter emitter, Supplier<Direction> face, Supplier<ParticleOptions> particle, BooleanSupplier running, float chance,
                               double minHeight, double maxHeight, double width)
    {
        _emitter = emitter;
        _face = face;
        _particle = particle;
        _running = running;
        _chance = chance;
        _minHeight = minHeight;
        _maxHeight = maxHeight;
        _width = width;
    }

    @Override
    public void tick()
    {
        ParticleOptions options = _particle.get();

        if (!_running.getAsBoolean() || _randomSource.nextFloat() >= _chance || options == null) return;

        Direction facing = _face.get();
        Vec3 out = new Vec3(facing.getStepX(), 0.0, facing.getStepZ());
        Vec3 side = new Vec3(facing.getClockWise().getStepX(), 0.0, facing.getClockWise().getStepZ());
        double height = _minHeight + _randomSource.nextDouble() * (_maxHeight - _minHeight);
        Vec3 offset = out.scale(FaceOffset).add(side.scale((_randomSource.nextDouble() - 0.5) * _width)).add(0.0, height - 0.5, 0.0);
        Vec3 velocity = out.scale(getBetween(MinOutwardSpeed, MaxOutwardSpeed)).add(side.scale((_randomSource.nextDouble() - 0.5) * SidewaysSpeed))
                .add(0.0, getBetween(MinUpwardSpeed, MaxUpwardSpeed), 0.0);

        _emitter.emit(options, offset, velocity);
    }

    private double getBetween(double min, double max) { return min + _randomSource.nextDouble() * (max - min); }
}
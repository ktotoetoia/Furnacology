package com.im.visuals.particles;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.tags.ITagLibrary;
import com.im.core.tags.Tag;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ParticleEmitter
{
    private static final double SurfaceOffset = 0.55;

    private final ICompositeBlockEntity _entity;
    private final Supplier<? extends ITagLibrary<ParticleSet>> _library;
    private final RandomSource _random = RandomSource.create();

    public ParticleEmitter(ICompositeBlockEntity entity, Supplier<? extends ITagLibrary<ParticleSet>> library)
    {
        _entity = entity;
        _library = library;
    }

    public void emit(ParticleOptions particle, Vec3 offset, Vec3 velocity)
    {
        Level level = _entity.getLevel();

        if (level == null || !level.isClientSide()) return;

        Vec3 position = Vec3.atCenterOf(_entity.getBlockPos()).add(offset);
        level.addParticle(particle, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }

    public void streak(Tag<ParticleSet> tag, Vec3 target, ParticleStreak streak)
    {
        Level level = _entity.getLevel();
        ParticleSet set =  _library.get().getRandom(tag, _random);

        if (level == null ||  set == null) return;

        streak.emit(level, set, getSurfaceToward(Vec3.atCenterOf(_entity.getBlockPos()), target), target, _random);
    }

    private static Vec3 getSurfaceToward(Vec3 center, Vec3 target)
    {
        Vec3 toward = target.subtract(center);
        double reach = Math.max(Math.abs(toward.x), Math.max(Math.abs(toward.y), Math.abs(toward.z)));

        return reach <= SurfaceOffset ? center : center.add(toward.scale(SurfaceOffset / reach));
    }
}
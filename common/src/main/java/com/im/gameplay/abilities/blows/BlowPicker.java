package com.im.gameplay.abilities.blows;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.tags.ITagLibrary;
import com.im.core.values.random.WeightedRandom;
import com.im.infrastructure.saving.ISyncSerializable;
import com.im.visuals.particles.FaceSprayer;
import com.im.visuals.particles.ParticleSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

public class BlowPicker implements ISyncSerializable
{
    private static final String TagBlow = "blow";

    private final ICompositeBlockEntity _entity;
    private final Supplier<List<Blow>> _available;
    private final Blow _fallback;
    private final Supplier<? extends ITagLibrary<ParticleSet>> _particles;
    private final FaceSprayer _sprayer = new FaceSprayer();
    private final RandomSource _random = RandomSource.create();
    private int _index = -1;

    public BlowPicker(ICompositeBlockEntity entity, Supplier<List<Blow>> available, Blow fallback, Supplier<? extends ITagLibrary<ParticleSet>> particles)
    {
        _entity = entity;
        _available = available;
        _fallback = fallback;
        _particles = particles;
    }

    public void captureSync(CompoundTag tag)  { tag.putInt(TagBlow, _index); }
    public void restoreSync(CompoundTag tag)  { _index = tag.getInt(TagBlow); }

    public Blow getCurrent()
    {
        List<Blow> blows = _available.get();
        return _index >= 0 &&_index < blows.size() ? blows.get(_index) : _fallback;
    }

    public void pick()
    {
        WeightedRandom<Blow> blows = new WeightedRandom<>();

        _available.get().forEach(blow ->  blows.add(blow, () -> (double) blow.weight().get()));
        _index = blows.nextIndex(_random);
        _entity.requestSync();
    }

    public void spray(ToDoubleFunction<Vec3> reach)
    {
        BlowParticles particles = getCurrent().particles();
        Level level = _entity.getEntityLevel();
        ParticleSet set = particles == null || level == null ? null : _particles.get().getRandom(particles.set(), _random);

        if (set == null) return;

        for (int band = 0;  band < Math.min(particles.bands().size(), set.sizes().size()); band++)
        {
            _sprayer.spray(level, _entity.getEntityPos(), _entity.getFacing(), particles.across(), particles.height(), particles.bands().get(band), set.sizes().get(band), reach);
        }
    }
}
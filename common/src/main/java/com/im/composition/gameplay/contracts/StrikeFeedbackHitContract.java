package com.im.composition.gameplay.contracts;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.tags.Tag;
import com.im.visuals.audio.SoundEmitter;
import com.im.visuals.particles.ParticleEmitter;
import com.im.visuals.particles.ParticleSet;
import com.im.visuals.particles.ParticleStreak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class StrikeFeedbackHitContract implements IHitContract
{
    private final Tag<SoundEvent> _soundTag;
    private final Tag<ParticleSet> _particles;
    private final ParticleStreak particleStreak;

    public StrikeFeedbackHitContract(Tag<SoundEvent> sound, Tag<ParticleSet> particles, ParticleStreak streak)
    {
        _soundTag = sound;
        _particles = particles;
        particleStreak = streak;
    }

    public void onHit(ICompositeBlockEntity source, List<LivingEntity> hits)
    {
        source.tryGet(SoundEmitter.class)
                .ifPresent(x -> x.cue(_soundTag));
        source.tryGet(ParticleEmitter.class)
                .ifPresent(x -> hits.forEach(hit -> x.streak(_particles, hit.getBoundingBox().getCenter(), particleStreak)));
    }
}
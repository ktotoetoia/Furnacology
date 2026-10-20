package com.im.composition.gameplay.abilities;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.abilities.IAbility;
import com.im.visuals.audio.SoundEmitter;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class PlaySoundAbility implements  IAbility
{
    private static final float MaxPitch = 2.0f;
    private static final float BasePitch = 0.8f;
    private static final float PitchPerDegree = 0.1f;

    private final Supplier<SoundEvent> _sound;

    public PlaySoundAbility(Supplier<SoundEvent> sound)
    {
        _sound = sound;
    }

    public void cast(ICompositeBlockEntity caster, int degree)
    {
        SoundEvent sound = _sound.get();

        if (sound != null)
            caster.tryGet(SoundEmitter.class ).ifPresent(
                    emitter -> emitter.cue(sound, Math.min(MaxPitch, BasePitch + PitchPerDegree * degree), null));
    }
}
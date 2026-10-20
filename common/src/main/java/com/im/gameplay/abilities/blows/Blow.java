package com.im.gameplay.abilities.blows;

import com.im.core.tags.Tag;
import com.im.visuals.animation.clips.Clip;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record Blow(Supplier<Integer> weight, Tag<Clip> windupAnimation, Tag<Clip> animation, @Nullable Tag<SoundEvent> windupSound,
                   @Nullable Tag<SoundEvent> releaseSound, @Nullable BlowParticles particles)
{
    private static final Supplier<Integer> Even = () -> 1;

    public static Blow of(Tag<Clip> windupAnimation, Tag<Clip> animation) { return new Blow(Even, windupAnimation, animation, null, null, null); }
    public Blow withWeight(Supplier<Integer> weight) { return new Blow(weight, windupAnimation, animation, windupSound, releaseSound, particles); }
    public Blow withWindupSound(Tag<SoundEvent> windupSound) { return new Blow(weight, windupAnimation, animation, windupSound, releaseSound, particles); }
    public Blow withReleaseSound(Tag<SoundEvent> releaseSound) { return new Blow(weight, windupAnimation, animation, windupSound, releaseSound, particles); }
    public Blow withParticles(BlowParticles particles) { return new Blow(weight, windupAnimation, animation, windupSound, releaseSound, particles); }
}
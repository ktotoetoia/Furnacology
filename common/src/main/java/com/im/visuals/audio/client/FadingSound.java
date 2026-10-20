package com.im.visuals.audio.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;

public class FadingSound extends AbstractTickableSoundInstance
{
    private final IVolumeFalloff _falloff;
    private final BooleanSupplier _playing;
    private final float _fadeInStep;
    private final float _fadeOutStep;
    private float _fade;

    private FadingSound(SoundEvent sound, SoundSource source, Vec3 position, IVolumeFalloff falloff, BooleanSupplier playing, int fadeInTicks, int fadeOutTicks)
    {
        super(sound, source, SoundInstance.createUnseededRandom());

        _falloff = falloff;
        _playing = playing;
        _fadeInStep = getStep(fadeInTicks);
        _fadeOutStep = getStep(fadeOutTicks);
        _fade = fadeInTicks > 0 ? 0.0f : 1.0f;

        x = position.x;
        y = position.y;
        z = position.z;

        attenuation = Attenuation.NONE;
        volume = getCurrentVolume();
    }

    public static FadingSound once(SoundEvent sound, SoundSource source, Vec3 position, IVolumeFalloff falloff, float pitch, BooleanSupplier playing, int fadeOutTicks)
    {
        FadingSound instance = new FadingSound(sound, source, position, falloff, playing, 0, fadeOutTicks);
        instance.pitch = pitch;
        return instance;
    }

    public static FadingSound loop(SoundEvent sound, SoundSource source, Vec3 position, IVolumeFalloff falloff, BooleanSupplier playing, int fadeInTicks, int fadeOutTicks)
    {
        FadingSound instance = new FadingSound(sound, source, position, falloff, playing, fadeInTicks, fadeOutTicks);
        instance.looping = true;
        instance.delay = 0;
        return instance;
    }

    @Override
    public boolean canStartSilent() { return looping; }

    public void tick()
    {
        if (_playing.getAsBoolean())
        {
            _fade = Math.min(1.0f, _fade + _fadeInStep);
        }
        else
        {
            _fade -= _fadeOutStep;

            if (_fade <= 0.0f)
            {
                stop();
                return;
            }
        }

        volume = getCurrentVolume();
    }

    private float getCurrentVolume()
    {
        Vec3 listener = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        return _fade * _falloff.getVolumeAt(listener.distanceTo(new Vec3(x, y, z)));
    }

    private static float getStep(int ticks) { return ticks > 0 ? 1.0f / ticks : 1.0f; }
}
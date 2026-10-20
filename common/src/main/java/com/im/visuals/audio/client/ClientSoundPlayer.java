package com.im.visuals.audio.client;

import com.im.visuals.audio.ISoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ClientSoundPlayer implements ISoundPlayer
{
    private final SoundSource _source;
    private final IVolumeFalloff _falloff;
    private final Supplier<Double> _rateInfluence;
    private final float _pitchSpread;
    private final RandomSource _random = RandomSource.create();

    public ClientSoundPlayer(SoundSource source, IVolumeFalloff falloff, Supplier<Double> rateInfluence, float pitchSpread)
    {
        _source = source;
        _falloff = falloff;
        _rateInfluence = rateInfluence;
        _pitchSpread = pitchSpread;
    }

    @Override
    public void play(SoundEvent sound, BlockPos pos, float rate, BooleanSupplier playing, int fadeOutTicks)
    {
        float speed = (float) (1.0 + (rate - 1.0) * _rateInfluence.get());
        float pitch = speed * (1.0f + (_random.nextFloat() * 2.0f - 1.0f) *  _pitchSpread);

        Minecraft.getInstance().getSoundManager().play(FadingSound.once(sound, _source, Vec3.atCenterOf(pos), _falloff, pitch, playing, fadeOutTicks));
    }
    @Override
    public void loop(SoundEvent sound, BlockPos pos, BooleanSupplier playing, int fadeInTicks, int fadeOutTicks)
    {
        Minecraft.getInstance().getSoundManager().play(FadingSound.loop(sound, _source, Vec3.atCenterOf(pos), _falloff, playing, fadeInTicks, fadeOutTicks));
    }
}

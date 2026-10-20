package com.im.visuals.audio;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;

import java.util.function.BooleanSupplier;

public interface ISoundPlayer
{
    ISoundPlayer Silent = new ISoundPlayer() { };

    default void play(SoundEvent sound, BlockPos pos, float rate, BooleanSupplier playing, int fadeOutTicks) { }
    default void loop(SoundEvent sound, BlockPos pos, BooleanSupplier playing, int fadeInTicks, int fadeOutTicks) { }
}
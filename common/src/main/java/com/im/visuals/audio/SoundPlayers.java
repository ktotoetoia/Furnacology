package com.im.visuals.audio;

import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public final class SoundPlayers
{
    private static volatile ISoundPlayer _client = ISoundPlayer.Silent;

    public static void installClient(ISoundPlayer player) { _client = player; }
    public static ISoundPlayer forLevel(@Nullable Level level) { return level != null && level.isClientSide() ? _client : ISoundPlayer.Silent; }
}
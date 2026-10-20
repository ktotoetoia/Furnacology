package com.im.composition.initialization.client;

import com.im.databases.entities.ParticleDatabase;
import com.im.databases.registries.BlockRegistry;
import com.im.infrastructure.platform.client.IParticleRegistrar;
import com.im.infrastructure.platform.client.IRendererRegistrar;
import com.im.infrastructure.platform.client.IScreenRegistrar;
import com.im.infrastructure.config.FurnacologyClientConfig;
import com.im.ui.client.ComponentScreen;
import com.im.visuals.audio.SoundPlayers;
import com.im.visuals.audio.client.ClientSoundPlayer;
import com.im.visuals.audio.client.DecibelFalloff;
import com.im.visuals.particles.client.TimedFlameProvider;
import com.im.visuals.rendering.client.FaceRenderer;
import net.minecraft.sounds.SoundSource;

public final class ClientInitializer
{
    private static final double FullVolumeDistance = 3.0;
    private static final double TaperFraction = 0.8;
    private static final float PitchSpread = 0.1f;
    private static final float PixelsPerBlock = 16.0f;
    private static final float AverageFlameSize = 0.3f;
    private static final int SmallBlowPixels = 3, MediumBlowPixels = 6, LargeBlowPixels = 7;

    public static void init()
    {
        DecibelFalloff falloff = new DecibelFalloff(FurnacologyClientConfig.SoundRange, FurnacologyClientConfig.SoundFalloff, FullVolumeDistance, TaperFraction);

        SoundPlayers.installClient(new ClientSoundPlayer(SoundSource.BLOCKS, falloff, FurnacologyClientConfig.SoundSpeedPitch, PitchSpread));
    }

    public static void registerScreens(IScreenRegistrar screens) { screens.register(BlockRegistry.ComponentMenuType.get(), ComponentScreen::new); }
    public static void registerRenderers(IRendererRegistrar renderers) { BlockRegistry.ComponentEntities.forEach(type -> renderers.register(type.get(), FaceRenderer::new)); }

    public static void registerParticles(IParticleRegistrar particles)
    {
        for (ParticleDatabase.BlowSet blow : ParticleDatabase.BlowSets)
        {
            particles.register(blow.small().get(), sprites -> new TimedFlameProvider(sprites, getScaleFor(SmallBlowPixels)));
            particles.register(blow.medium().get(), sprites -> new TimedFlameProvider(sprites, getScaleFor(MediumBlowPixels)));
            particles.register(blow.large().get(), sprites -> new TimedFlameProvider(sprites, getScaleFor(LargeBlowPixels)));
        }
    }

    private static float getScaleFor(int pixels) { return pixels / PixelsPerBlock / AverageFlameSize; }

    private ClientInitializer() { }
}
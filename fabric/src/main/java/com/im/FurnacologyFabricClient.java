package com.im;

import com.im.composition.initialization.client.ClientInitializer;
import com.im.infrastructure.platform.client.IParticleRegistrar;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public final class FurnacologyFabricClient implements ClientModInitializer
{
    public void onInitializeClient()
    {
        com.im.composition.initialization.client.ClientInitializer.init();
        com.im.composition.initialization.client.ClientInitializer.registerScreens(MenuScreens::register);
        com.im.composition.initialization.client.ClientInitializer.registerRenderers(BlockEntityRenderers::register);
        ClientInitializer.registerParticles(new IParticleRegistrar()
        {
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> provider)
            {
                ParticleFactoryRegistry.getInstance().register(type, (ParticleFactoryRegistry.PendingParticleFactory<T>) provider::apply);
            }
        });
    }
}

package com.im;

import com.im.composition.initialization.client.ClientInitializer;
import com.im.infrastructure.platform.client.IParticleRegistrar;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.function.Function;

@EventBusSubscriber(modid = Furnacology.ModId, value = Dist.CLIENT)
public final class FurnacologyNeoforgeClient
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) { com.im.composition.initialization.client.ClientInitializer.init(); }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) { com.im.composition.initialization.client.ClientInitializer.registerScreens(event::register); }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) { com.im.composition.initialization.client.ClientInitializer.registerRenderers(event::registerBlockEntityRenderer); }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event)
    {
        ClientInitializer.registerParticles(new IParticleRegistrar()
        {
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> provider)
            {
                event.registerSpriteSet(type, provider::apply);
            }
        });
    }

    private FurnacologyNeoforgeClient() { }
}
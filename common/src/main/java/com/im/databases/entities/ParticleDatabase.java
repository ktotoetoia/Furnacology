package com.im.databases.entities;

import com.im.Furnacology;
import com.im.core.tags.Tag;
import com.im.core.tags.TagLibrary;
import com.im.infrastructure.platform.Registrar;
import com.im.visuals.particles.ParticleSet;
import com.im.visuals.particles.TimedParticleOptions;
import com.im.visuals.particles.TimedParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public final class ParticleDatabase
{
    public static final Registrar<ParticleType<?>> Particles = new Registrar<>(BuiltInRegistries.PARTICLE_TYPE, Furnacology.ModId);

    public static final Tag<ParticleSet> BlowTag = new Tag<>("blow");
    public static final Tag<ParticleSet> SlimeBlowTag = new Tag<>("slime_blow");
    public static final Tag<ParticleSet> EmberStrikeTag = new Tag<>("ember_strike");

    public static final BlowSet FireBlow = new BlowSet("fire");
    public static final BlowSet EnderBlow = new BlowSet("ender");
    public static final BlowSet SlimeBlow = new BlowSet("slime");
    public static final List<BlowSet> BlowSets = List.of(FireBlow, EnderBlow, SlimeBlow);

    public static final TagLibrary<ParticleSet> LivingFurnace = new TagLibrary<ParticleSet>()
            .add(BlowTag, FireBlow.getParticles()).add(SlimeBlowTag, SlimeBlow.getParticles()).add(EmberStrikeTag, FireBlow.getParticles());
    public static final TagLibrary<ParticleSet> ReinforcedLivingFurnace = new TagLibrary<ParticleSet>().withParent(LivingFurnace);
    public static final TagLibrary<ParticleSet> EnderFurnace = new TagLibrary<ParticleSet>().add(BlowTag, EnderBlow.getParticles()).withParent(LivingFurnace);

    public record BlowSet(Supplier<TimedParticleType> small, Supplier<TimedParticleType> medium, Supplier<TimedParticleType> large)
    {
        public BlowSet(String name) { this(register(name + "_blow_small"), register(name + "_blow_medium"), register(name + "_blow_large")); }

        public ParticleSet getParticles() { return new ParticleSet(List.of(getTimed(small), getTimed(medium), getTimed(large))); }

        private static Supplier<TimedParticleType> register(String id) { return Particles.register(id, TimedParticleType::new); }
        private static IntFunction<TimedParticleOptions> getTimed(Supplier<TimedParticleType> type) { return lifetime -> new TimedParticleOptions(type.get(), lifetime); }
    }
}
package com.im.databases.buffs;

import com.im.Furnacology;
import com.im.composition.gameplay.abilities.BlastAbility;
import com.im.composition.gameplay.abilities.PlaySoundAbility;
import com.im.composition.gameplay.contracts.*;
import com.im.composition.gameplay.effects.AuraEffectModifier;
import com.im.composition.gameplay.effects.SpeedEffectModifier;
import com.im.core.lifecycle.database.Database;
import com.im.core.tags.Tag;
import com.im.core.values.random.RandomRange;
import com.im.core.values.scaling.ScaleModifier;
import com.im.databases.entities.ClipDatabase;
import com.im.databases.entities.ParticleDatabase;
import com.im.databases.entities.SoundDatabase;
import com.im.databases.registries.DataComponentRegistry;
import com.im.gameplay.abilities.ReleaseStyle;
import com.im.gameplay.abilities.blows.Blow;
import com.im.gameplay.abilities.blows.BlowParticles;
import com.im.gameplay.buffs.Buff;
import com.im.gameplay.digestion.digesters.*;
import com.im.gameplay.effects.effectfactories.IEffectModifierFactory;
import com.im.visuals.animation.clips.Clip;
import com.im.visuals.particles.ParticleBand;
import com.im.visuals.particles.ParticleSet;
import com.im.visuals.particles.ParticleStreak;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

import static com.im.infrastructure.config.FurnacologyConfig.*;

public final class BuffDatabase
{
    public static final ResourceLocation Speed = Furnacology.id("speed");
    public static final ResourceLocation Bellows = Furnacology.id("bellows");
    public static final ResourceLocation SlimeBellows = Furnacology.id("slime_bellows");
    public static final ResourceLocation StrongBellows = Furnacology.id("strong_bellows");
    public static final ResourceLocation Nourished = Furnacology.id("nourished");
    public static final ResourceLocation EmberStrike = Furnacology.id("ember_strike");
    private static final ResourceLocation BellowsIcon = Furnacology.id("buff/bellows");
    private static final ResourceLocation SlimeBellowsIcon = Furnacology.id("buff/slime_bellows");
    private static final ResourceLocation StrongBellowsIcon = Furnacology.id("buff/strong_bellows");
    private static final Supplier<Float> None = () -> 0.0f;
    private static final double StrikeSpread = 0.6;

    public static final Database<Buff> Buffs = new Database<Buff>()
            .add(Speed, getSpeed())
            .add(Bellows, getBellows(() -> BellowsForce.get().floatValue(), () -> BellowsDamage.get().floatValue(), () -> BellowsBurnSeconds.get().floatValue(),
                    ParticleDatabase.BlowTag, BellowsIcon))
            .add(SlimeBellows, getBellows(() -> BellowsForce.get().floatValue(), None, None, ParticleDatabase.SlimeBlowTag, SlimeBellowsIcon))
            .add(StrongBellows, getBellows(() -> (float) (BellowsForce.get() * StrongBellowsForceMultiplier.get()), () -> BellowsDamage.get().floatValue(),
                    () -> BellowsBurnSeconds.get().floatValue(), ParticleDatabase.BlowTag, StrongBellowsIcon))
            .add(Nourished, new Buff(getSpeedBonus(() -> FoodSpeedPerSaturation.get().floatValue())))
            .add(EmberStrike, getEmberStrike());

    public static final IDigester<ItemStack> BuffItemDigester = new ComponentDigester(DataComponentRegistry.BuffGrants);
    public static final IDigester<ItemStack> NourishingDigester = new FoodDigester(Nourished, FoodProperties::saturation, food -> food.nutrition() * FoodTicksPerNutrition.get());
    public static final IDigester<ItemStack> FurnaceDigester = new CompositeDigester<>(BuffItemDigester,
            new FilteredDigester<>(NourishingDigester, stack -> !stack.has(DataComponentRegistry.ReplacesFoodBuff.get())));

    private static Buff getSpeed()
    {
        return new Buff(List.of(context -> new AuraEffectModifier(context.target(), MobEffects.MOVEMENT_SPEED, SpeedBuffAmplifier::get, SpeedBuffReapplyTicks::get)),
                new PlaySoundAbility(() -> BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(SpeedBuffSound.get()))),
                new ReleaseStyle(SpeedBuffReleaseTicks::get, Blow.of(ClipDatabase.Lit, ClipDatabase.Smelt).withWindupSound(SoundDatabase.Inhale)));
    }

    private static Buff getEmberStrike()
    {
        IHitContract onHit = new KnockbackHitContract(() -> EmberStrikeKnockback.get().floatValue()).then(new StrikeFeedbackHitContract(SoundDatabase.EmberStrike,
                ParticleDatabase.EmberStrikeTag, new ParticleStreak(EmberStrikeParticles::get, EmberStrikeParticleTicks::get, StrikeSpread)));
        ISmeltContract contract = new AreaDamageSmeltContract(entity -> entity instanceof Enemy, () -> EmberStrikeDamage.get().floatValue(), None, onHit);

        return new Buff(context -> contract);
    }

    private static Buff getBellows(Supplier<Float> force, Supplier<Float> damage, Supplier<Float> burnSeconds, Tag<ParticleSet> particles, ResourceLocation icon)
    {
        return new Buff(List.of(getSpeedBonus(() -> BellowsSpeedBonus.get().floatValue())),
                new BlastAbility(force, damage, burnSeconds, BellowsRange::get, BellowsWidth::get),
                new ReleaseStyle(BellowsReleaseTicks::get, List.of(
                        getBellowsBlow(RegularBlow, ClipDatabase.BlowBegin, ClipDatabase.Blow, SoundDatabase.Blow, particles),
                        getBellowsBlow(WideBlow, ClipDatabase.WideBlowBegin, ClipDatabase.WideBlow, SoundDatabase.WideBlow, particles)), icon));
    }

    private static Blow getBellowsBlow(BlowSettings settings, Tag<Clip> windup, Tag<Clip> animation, Tag<SoundEvent> sound, Tag<ParticleSet> particles)
    {
        return Blow.of(windup, animation).withWeight(settings.weight()::get).withWindupSound(SoundDatabase.Inhale).withReleaseSound(sound)
                .withParticles(new BlowParticles(particles, new RandomRange(settings.faceMinX()::get, settings.faceMaxX()::get),
                        new RandomRange(settings.faceMinY()::get, settings.faceMaxY()::get), List.of(getBand(settings.small()), getBand(settings.medium()), getBand(settings.large()))));
    }

    private static ParticleBand getBand(BandSettings settings)
    {
        return new ParticleBand(new RandomRange(settings.spawnFrom()::get, settings.spawnTo()::get), new RandomRange(settings.minSpeed()::get, settings.maxSpeed()::get),
                new RandomRange(settings.minRise()::get, settings.maxRise()::get),
                new RandomRange(() -> (double) settings.minLifetime().get(), () -> (double) settings.maxLifetime().get()), settings.perTick()::get);
    }

    private static IEffectModifierFactory getSpeedBonus(Supplier<Float> bonus) { return context -> new SpeedEffectModifier(new ScaleModifier(bonus.get() * context.potency())); }
}
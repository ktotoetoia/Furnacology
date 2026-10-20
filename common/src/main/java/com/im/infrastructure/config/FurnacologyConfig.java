package com.im.infrastructure.config;

import com.im.gameplay.smelting.memory.SpeedCurve;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

//TODO refactor the config, remove ability specific fields and create a tag matching list so abilities will use the tag or name to get their stats rather than depending on a specific field
//this will make the config less messy and more organized
//this might require creating a separate json file and then deserializing it
public final class FurnacologyConfig
{
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SpeedSteps;
    public static final ModConfigSpec.IntValue BuffChargeTicks;
    public static final ModConfigSpec.IntValue BuffReleaseDelayTicks;
    public static final ModConfigSpec.DoubleValue AreaRadius;
    public static final ModConfigSpec.IntValue SpeedBuffAmplifier;
    public static final ModConfigSpec.IntValue SpeedBuffReapplyTicks;
    public static final ModConfigSpec.IntValue SpeedBuffReleaseTicks;
    public static final ModConfigSpec.ConfigValue<String> SpeedBuffSound;
    public static final ModConfigSpec.DoubleValue BellowsSpeedBonus;
    public static final ModConfigSpec.IntValue BellowsReleaseTicks;
    public static final ModConfigSpec.DoubleValue BellowsForce;
    public static final ModConfigSpec.DoubleValue BellowsDamage;
    public static final ModConfigSpec.DoubleValue BellowsBurnSeconds;
    public static final ModConfigSpec.IntValue BellowsRange;
    public static final ModConfigSpec.IntValue BellowsWidth;
    public static final ModConfigSpec.DoubleValue StrongBellowsForceMultiplier;
    public static final ModConfigSpec.DoubleValue EmberStrikeDamage;
    public static final ModConfigSpec.DoubleValue EmberStrikeKnockback;
    public static final ModConfigSpec.IntValue EmberStrikeParticles;
    public static final ModConfigSpec.IntValue EmberStrikeParticleTicks;
    public static final ModConfigSpec.DoubleValue FoodSpeedPerSaturation;
    public static final ModConfigSpec.IntValue FoodTicksPerNutrition;
    public static final ModConfigSpec.DoubleValue PerfectCoalItemsSmelted;
    public static final ModConfigSpec.DoubleValue PerfectCoalDropChance;
    public static final ModConfigSpec.DoubleValue PerfectCoalFortuneBonus;
    public static final ModConfigSpec.DoubleValue WitchHutLivingFurnaceChance;
    public static final ModConfigSpec.DoubleValue VillageLivingFurnaceChance;
    public static final ModConfigSpec.IntValue RareIdleRarity;
    public static final BlowSettings RegularBlow;
    public static final BlowSettings WideBlow;
    public static final ModConfigSpec Spec;

    private static List<String> _parsedSteps = List.of();
    private static SpeedCurve _speedCurve = SpeedCurve.Default;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("smelting");
        SpeedSteps = builder.comment("How a furnace speeds up as it smelts more of the same item, as \"smelts:speed\" steps.",
                        "0.75x from the start, 1.00x at 8, 1.25x at 24 and 1.50x at 56 total smelts.")
                .defineList("speedSteps", SpeedCurve.DefaultEntries, () -> "0:1.00", SpeedCurve::isValidEntry);
        builder.pop();

        builder.push("buffs");
        BuffChargeTicks = builder.comment("How many ticks it takes for the furnace to digest a food item").defineInRange("chargeTicks", 100, 1, 72000);
        BuffReleaseDelayTicks = builder.comment("Ticks after the furnace stops working before stored instant buffs go off")
                .defineInRange("releaseDelayTicks", 20, 0, 1200);
        builder.pop();

        builder.push("area");
        AreaRadius = builder.comment("Radius around a furnace that its area effects can reach,",
                        "coal burger buff uses this area to search for enemies to deal damage. Spiky Netherite adds 10 to it and the Dragon Chalice 15.")
                .defineInRange("radius", 8.0, 1.0, 64.0);
        builder.pop();

        builder.push("speedBuff");
        SpeedBuffAmplifier = builder.defineInRange("amplifier", 1, 0, 255);
        SpeedBuffReapplyTicks = builder.defineInRange("reapplyTicks", 40, 1, 1200);
        SpeedBuffReleaseTicks = builder.defineInRange("releaseTicks", 60, 1, 1200);
        SpeedBuffSound = builder.define("sound", "minecraft:entity.player.levelup");
        builder.pop();

        builder.push("bellowsBuff");
        BellowsSpeedBonus = builder.defineInRange("speedBonus", 1.0, 0.0, 100.0);
        BellowsReleaseTicks = builder.defineInRange("releaseTicks", 60, 1, 1200);
        BellowsForce = builder.defineInRange("force", 0.4, 0.0, 10.0);
        BellowsDamage = builder.defineInRange("damage", 2.0, 0.0, 1000.0);
        BellowsBurnSeconds = builder.defineInRange("burnSeconds", 4.0, 0.0, 600.0);
        BellowsRange = builder.defineInRange("range", 6, 1, 64);
        BellowsWidth = builder.defineInRange("width", 3, 1, 64);
        StrongBellowsForceMultiplier = builder.defineInRange("strongForceMultiplier", 1.75, 0.0, 10.0);
        builder.pop();

        builder.push("emberStrikeBuff");
        EmberStrikeDamage = builder.defineInRange("damage", 2.0, 0.0, 1000.0);
        EmberStrikeKnockback = builder.defineInRange("knockback", 0.5, 0.0, 10.0);
        EmberStrikeParticles = builder.defineInRange("particlesPerTarget", 8, 0, 64);
        EmberStrikeParticleTicks = builder.defineInRange("particleTicks", 3, 1, 20);
        builder.pop();

        builder.push("foodBuff");
        FoodSpeedPerSaturation = builder.comment("Speed added to the furnace per point of saturation the digested food restores")
                .defineInRange("speedPerSaturation", 0.05, 0.0, 10.0);
        FoodTicksPerNutrition = builder.comment("Ticks the speed lasts per point of hunger the digested food restores. Steak restores 8, bread 5")
                .defineInRange("ticksPerNutrition", 100, 1, 72000);
        builder.pop();

        builder.push("perfectCoal");
        PerfectCoalItemsSmelted = builder.comment("How many items a perfect coal chunk smelts in a regular furnace. Coal smelts 8. 16.18 is a golden ratio")
                .defineInRange("itemsSmelted", 16.18, 0.0, 10000.0);
        PerfectCoalDropChance = builder.comment("Chance, from 0 to 1, that mining a coal ore also drops a perfect coal chunk",
                        "Coal ores are the blocks with the furnacology:perfect_coal_ores block tag, by default its coal ore and deepslate coal ore")
                .defineInRange("dropChance", 0.05, 0.0, 1.0);
        PerfectCoalFortuneBonus = builder.comment("Added to the drop chance for each level of Fortune").defineInRange("fortuneBonus", 0.025, 0.0, 1.0);
        builder.pop();

        builder.push("worldgen");
        WitchHutLivingFurnaceChance = builder.comment("Chance, from 0 to 1, that a cauldron in a witch hut generates as a living furnace instead.")
                .defineInRange("witchHutChance", 0.5, 0.0, 1.0);
        VillageLivingFurnaceChance = builder.comment("Chance, from 0 to 1, that each furnace in a newly generated village can generate as a living furnace instead.")
                .defineInRange("villageChance", 0.15, 0.0, 1.0);
        builder.pop();

        builder.push("animations");
        RareIdleRarity = builder.comment("How many times less often each rare idle animation plays than each regular one.")
                .defineInRange("rareIdleRarity", 10, 1, 1000);
        builder.pop();

        builder.comment("The blows a furnace can release a bellows buff with. Each release picks one of them at random by weight.").push("bellowsBlows");
        RegularBlow = blow(builder, "regular", "A narrow stream of fire from the middle of the furnace's front", 7.0, 9.0,
                new BandDefaults(0.0, 50.0, 0.10, 0.20, 0.0, 0.02, 8, 14, 6.0),
                new BandDefaults(30.0, 70.0, 0.05, 0.10, 0.01, 0.04, 14, 22, 3.0),
                new BandDefaults(60.0, 100.0, 0.03, 0.05, 0.03, 0.06, 20, 30, 1.5));
        WideBlow = blow(builder, "wide", "A wider narrow stream of bigger flames from most of the furnace's front", 4.0, 12.0,
                new BandDefaults(0.0, 50.0, 0.10, 0.20, 0.0, 0.02, 8, 14, 2.0),
                new BandDefaults(30.0, 70.0, 0.05, 0.10, 0.01, 0.04, 14, 22, 5.0),
                new BandDefaults(60.0, 100.0, 0.03, 0.05, 0.03, 0.06, 20, 30, 3.0));
        builder.pop();

        Spec = builder.build();
    }

    public static int rareIdleRarity() { return Spec.isLoaded() ? RareIdleRarity.get() : RareIdleRarity.getDefault(); }

    public static SpeedCurve speedCurve()
    {
        if (!Spec.isLoaded()) return SpeedCurve.Default;

        List<? extends String> steps = SpeedSteps.get();

        if (!steps.equals(_parsedSteps))
        {
            _parsedSteps = new ArrayList<>(steps);
            _speedCurve = SpeedCurve.parse(steps);
        }

        return _speedCurve;
    }

    private static BlowSettings blow(ModConfigSpec.Builder builder, String name, String description, double faceMin, double faceMax,
                                     BandDefaults small, BandDefaults medium, BandDefaults large)
    {
        builder.comment(description).push(name);

        BlowSettings blow = new BlowSettings(
                builder.comment("How likely this blow is compared to the others. 0 disables it.").defineInRange("weight", 1, 0, 1000),
                builder.comment("Area of the furnace's front the particles start from, in pixels (0 to 16) seen from the front,",
                        "with x from the left edge and y from the bottom edge.").defineInRange("faceMinX", faceMin, 0.0, 16.0),
                builder.defineInRange("faceMaxX", faceMax, 0.0, 16.0),
                builder.defineInRange("faceMinY", faceMin, 0.0, 16.0),
                builder.defineInRange("faceMaxY", faceMax, 0.0, 16.0),
                band(builder, "small", small),
                band(builder, "medium", medium),
                band(builder, "large", large));

        builder.pop();
        return blow;
    }

    private static BandSettings band(ModConfigSpec.Builder builder, String name, BandDefaults defaults)
    {
        builder.push(name);

        BandSettings band = new BandSettings(
                builder.comment("Where along the blow they appear, as a percentage of its range: 0 is the furnace's front, 100 the",
                        "far end. The ranges of different sizes may overlap.").defineInRange("spawnFrom", defaults.spawnFrom(), 0.0, 100.0),
                builder.defineInRange("spawnTo", defaults.spawnTo(), 0.0, 100.0),
                builder.comment("Blocks per tick they fly away from the furnace at.").defineInRange("minSpeed", defaults.minSpeed(), 0.0, 10.0),
                builder.defineInRange("maxSpeed", defaults.maxSpeed(), 0.0, 10.0),
                builder.comment("Blocks per tick they rise at.").defineInRange("minRise", defaults.minRise(), -10.0, 10.0),
                builder.defineInRange("maxRise", defaults.maxRise(), -10.0, 10.0),
                builder.comment("Ticks they last before fading out.").defineInRange("minLifetime", defaults.minLifetime(), 1, 1200),
                builder.defineInRange("maxLifetime", defaults.maxLifetime(), 1, 1200),
                builder.comment("How many appear each tick of the blow. A fraction is a chance, so 0.5 is one every other tick.")
                        .defineInRange("perTick", defaults.perTick(), 0.0, 100.0));

        builder.pop();
        return band;
    }

    public record BlowSettings(ModConfigSpec.IntValue weight, ModConfigSpec.DoubleValue faceMinX, ModConfigSpec.DoubleValue faceMaxX,
                               ModConfigSpec.DoubleValue faceMinY, ModConfigSpec.DoubleValue faceMaxY, BandSettings small, BandSettings medium,
                               BandSettings large) { }

    public record BandSettings(ModConfigSpec.DoubleValue spawnFrom, ModConfigSpec.DoubleValue spawnTo, ModConfigSpec.DoubleValue minSpeed,
                               ModConfigSpec.DoubleValue maxSpeed, ModConfigSpec.DoubleValue minRise, ModConfigSpec.DoubleValue maxRise,
                               ModConfigSpec.IntValue minLifetime, ModConfigSpec.IntValue maxLifetime, ModConfigSpec.DoubleValue perTick) { }

    private record BandDefaults(double spawnFrom, double spawnTo, double minSpeed, double maxSpeed, double minRise, double maxRise,
                                int minLifetime, int maxLifetime, double perTick) { }

    private FurnacologyConfig() { }
}
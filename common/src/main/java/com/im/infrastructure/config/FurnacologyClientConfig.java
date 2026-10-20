package com.im.infrastructure.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class FurnacologyClientConfig
{
    public static final ModConfigSpec.DoubleValue SoundRange;
    public static final ModConfigSpec.DoubleValue SoundFalloff;
    public static final ModConfigSpec.DoubleValue SoundSpeedPitch;
    public static final ModConfigSpec.BooleanValue ChewingSounds;
    public static final ModConfigSpec.BooleanValue SmeltingSounds;
    public static final ModConfigSpec.BooleanValue BlowingSounds;
    public static final ModConfigSpec.BooleanValue EmberStrikeSounds;
    public static final ModConfigSpec Spec;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("sounds");

        SoundRange = builder.comment("Blocks away a furnace can still be heard from.").defineInRange("range", 6.0, 2.0, 128.0);
        SoundFalloff = builder.comment("Sound falloff").defineInRange("falloff", 0, 0.0, 30.0);
        SoundSpeedPitch = builder.comment("How much a faster furnace also speeds up its chewing and smelting sounds. 1 plays them as much")
                .defineInRange("speedPitch", 1.0, 0.0, 2.0);
        ChewingSounds = builder.comment("Whether a furnace makes its chewing sound while it smelts.").define("chewing", true);
        SmeltingSounds = builder.comment("Whether a furnace makes its gulping sound each time it finishes smelting an item, or throws it out.")
                .define("smelting", true);
        BlowingSounds = builder.comment("Whether a furnace makes its blowing sounds").define("blowing", true);
        EmberStrikeSounds = builder.comment("Whether a furnace makes its Ember Strike sound when a smelt hurts the monsters around it")
                .define("emberStrike", true);

        builder.pop();

        Spec = builder.build();
    }

    private FurnacologyClientConfig() { }
}
package com.im.databases.entities;

import com.im.Furnacology;
import com.im.core.tags.Tag;
import com.im.core.tags.TagLibrary;
import com.im.infrastructure.platform.Registrar;
import com.im.infrastructure.config.FurnacologyClientConfig;
import com.im.visuals.audio.SoundScanner;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;

public final class SoundDatabase
{
    public static final Registrar<SoundEvent> Sounds = new Registrar<>(BuiltInRegistries.SOUND_EVENT, Furnacology.ModId);

    public static final Tag<SoundEvent> Bite = new Tag<>("bite");
    public static final Tag<SoundEvent> Smelt = new Tag<>("smelt");
    public static final Tag<SoundEvent> Throw = new Tag<>("throw", Smelt);
    public static final Tag<SoundEvent> Inhale = new Tag<>("inhale");
    public static final Tag<SoundEvent> Blow = new Tag<>("blow");
    public static final Tag<SoundEvent> WideBlow = new Tag<>("wide_blow", Blow);
    public static final Tag<SoundEvent> EmberStrike = new Tag<>("ember_strike");

    private static final SoundScanner Scanner = new SoundScanner(Furnacology.ModId);
    private static final List<SoundScanner.Entry> FurnaceSounds = List.of(SoundScanner.Entry.of(Bite), SoundScanner.Entry.of(Smelt), SoundScanner.Entry.of(Throw),
            SoundScanner.Entry.of(Inhale, () -> SoundEvents.BREEZE_INHALE), SoundScanner.Entry.of(Blow), SoundScanner.Entry.of(WideBlow), SoundScanner.Entry.of(EmberStrike));

    public static final TagLibrary<SoundEvent> LivingFurnace = Scanner.library("block.living_furnace", FurnaceSounds, null);
    public static final TagLibrary<SoundEvent> ReinforcedLivingFurnace = Scanner.library("block.reinforced_living_furnace", FurnaceSounds, LivingFurnace);
    public static final TagLibrary<SoundEvent> EnderFurnace = Scanner.library("block.ender_furnace", FurnaceSounds, LivingFurnace);

    private static final Map<String, ModConfigSpec.BooleanValue> Toggles = Map.of(
            Bite.name(), FurnacologyClientConfig.ChewingSounds,
            Smelt.name(), FurnacologyClientConfig.SmeltingSounds,
            Throw.name(), FurnacologyClientConfig.SmeltingSounds,
            Inhale.name(), FurnacologyClientConfig.BlowingSounds,
            Blow.name(), FurnacologyClientConfig.BlowingSounds,
            WideBlow.name(), FurnacologyClientConfig.BlowingSounds,
            EmberStrike.name(), FurnacologyClientConfig.EmberStrikeSounds);

    static
    {
        for (String sound : List.of("bite", "smelt", "throw", "blow", "wide_blow", "ember_strike"))
        {
            String name = "block.living_furnace." + sound;
            Sounds.register(name, () -> SoundEvent.createVariableRangeEvent(Furnacology.id(name)));
        }
    }

    public static boolean isEnabled(Tag<SoundEvent> tag)
    {
        ModConfigSpec.BooleanValue toggle = Toggles.get(tag.name());
        return toggle == null || !FurnacologyClientConfig.Spec.isLoaded() || toggle.get();
    }
}
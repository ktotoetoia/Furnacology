package com.im.databases.entities;

import com.im.Furnacology;
import com.im.gameplay.skins.FurnaceSkin;
import com.im.ui.hud.FurnaceGui;

import java.util.List;

public final class SkinDatabase
{
    public static final FurnaceSkin LivingFurnace = new FurnaceSkin(Furnacology.id("living_furnace"), ClipDatabase.LivingFurnace, SoundDatabase.LivingFurnace,
            FurnaceGui.of("living_furnace", null), ParticleDatabase.LivingFurnace);
    public static final FurnaceSkin ReinforcedLivingFurnace = new FurnaceSkin(Furnacology.id("reinforced_living_furnace"), ClipDatabase.ReinforcedLivingFurnace,
            SoundDatabase.ReinforcedLivingFurnace, FurnaceGui.of("reinforced_living_furnace", LivingFurnace.gui()), ParticleDatabase.ReinforcedLivingFurnace);
    public static final FurnaceSkin EnderFurnace = new FurnaceSkin(Furnacology.id("ender_furnace"), ClipDatabase.EnderFurnace, SoundDatabase.EnderFurnace,
            FurnaceGui.of("ender_furnace", LivingFurnace.gui()), ParticleDatabase.EnderFurnace);
    public static final List<FurnaceSkin> FurnaceSkins = List.of(LivingFurnace, ReinforcedLivingFurnace, EnderFurnace);
}
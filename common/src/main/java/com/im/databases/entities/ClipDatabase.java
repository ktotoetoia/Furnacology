package com.im.databases.entities;

import com.im.Furnacology;
import com.im.core.tags.ITagLibrary;
import com.im.core.tags.Tag;
import com.im.core.tags.TagLibrary;
import com.im.infrastructure.config.FurnacologyConfig;
import com.im.visuals.animation.clips.Clip;
import com.im.visuals.animation.clips.ClipPattern;
import com.im.visuals.animation.clips.ClipScanner;
import com.im.visuals.animation.clips.TextureFrames;

import javax.annotation.Nullable;
import java.util.List;

public final class ClipDatabase
{
    private static final float FaceTicksPerFrame = 7.0f;
    private static final float WindupTicksPerFrame = 4.0f;
    private static final float BlowTicks = 60.5f;
    private static final float IdleTicksPerFrame = 4.0f;
    private static final ClipScanner Scanner = new ClipScanner(TextureFrames::count);

    public static final Tag<Clip> Lit = new Tag<>("lit");
    public static final Tag<Clip> Smelt = new Tag<>("smelt");
    public static final Tag<Clip> Idle = new Tag<>("idle");
    public static final Tag<Clip> Throw = new Tag<>("throw", Smelt);
    public static final Tag<Clip> BlowBegin = new Tag<>("blow_begin", Lit);
    public static final Tag<Clip> Blow = new Tag<>("blow", Smelt);
    public static final Tag<Clip> WideBlowBegin = new Tag<>("wide_blow_begin", BlowBegin);
    public static final Tag<Clip> WideBlow = new Tag<>("wide_blow", Blow);

    private static final List<ClipScanner.Entry> FurnaceClips = List.of(
            ClipScanner.Entry.of(Lit, ClipPattern.of("lit", FaceTicksPerFrame)),
            ClipScanner.Entry.of(Smelt, ClipPattern.of("smelt", FaceTicksPerFrame)),
            ClipScanner.Entry.of(Throw, ClipPattern.of("throw", FaceTicksPerFrame)),
            ClipScanner.Entry.of(Idle, ClipPattern.of("idle", IdleTicksPerFrame), ClipPattern.of("rare_idle", IdleTicksPerFrame).withWeight(ClipDatabase::getRareWeight)),
            ClipScanner.Entry.of(BlowBegin, ClipPattern.of("blow_begin", WindupTicksPerFrame)),
            ClipScanner.Entry.of(Blow, ClipPattern.lasting("blow", BlowTicks)),
            ClipScanner.Entry.of(WideBlowBegin, ClipPattern.of("wide_blow_begin", WindupTicksPerFrame)),
            ClipScanner.Entry.of(WideBlow, ClipPattern.lasting("wide_blow", BlowTicks)));

    public static final TagLibrary<Clip> LivingFurnace = scan("living_furnace", null);
    public static final TagLibrary<Clip> ReinforcedLivingFurnace = scan("reinforced_living_furnace", LivingFurnace);
    public static final TagLibrary<Clip> EnderFurnace = scan("ender_furnace", LivingFurnace);

    private static TagLibrary<Clip> scan(String block, @Nullable ITagLibrary<Clip> parent) { return Scanner.library(Furnacology.id("block/" + block), FurnaceClips, parent); }
    private static double getRareWeight() { return 1.0 / FurnacologyConfig.rareIdleRarity(); }
}
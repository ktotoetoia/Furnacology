package com.im.gameplay.skins;

import com.im.core.tags.ITagLibrary;
import com.im.ui.hud.FurnaceGui;
import com.im.visuals.animation.clips.Clip;
import com.im.visuals.particles.ParticleSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public record FurnaceSkin(ResourceLocation id, ITagLibrary<Clip> clips, ITagLibrary<SoundEvent> sounds, FurnaceGui gui, ITagLibrary<ParticleSet> particles)
        implements ISkin { }
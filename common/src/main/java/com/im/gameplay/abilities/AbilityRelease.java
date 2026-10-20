package com.im.gameplay.abilities;

import com.im.gameplay.buffs.Buff;
import net.minecraft.resources.ResourceLocation;

public record AbilityRelease(ResourceLocation id, Buff buff, int degree, int duration) { }
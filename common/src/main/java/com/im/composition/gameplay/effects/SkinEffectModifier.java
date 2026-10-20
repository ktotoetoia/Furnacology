package com.im.composition.gameplay.effects;

import com.im.gameplay.effects.modifiers.IEffectModifier;
import com.im.gameplay.skins.ISkinRequest;
import net.minecraft.resources.ResourceLocation;

public record SkinEffectModifier(ResourceLocation skin, int priority) implements IEffectModifier, ISkinRequest { }

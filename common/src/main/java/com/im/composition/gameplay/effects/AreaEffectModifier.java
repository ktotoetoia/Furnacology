package com.im.composition.gameplay.effects;

import com.im.core.values.scaling.IScaleModifier;
import com.im.gameplay.effects.modifiers.IEffectModifier;

public record AreaEffectModifier(IScaleModifier radius) implements IEffectModifier { }

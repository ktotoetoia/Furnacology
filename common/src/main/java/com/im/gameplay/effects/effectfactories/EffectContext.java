package com.im.gameplay.effects.effectfactories;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;

public record EffectContext(ICompositeBlockEntity target, float potency) implements IEffectContext { }

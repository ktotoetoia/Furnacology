package com.im.gameplay.effects.effectfactories;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;

public interface IEffectContext
{
    ICompositeBlockEntity target();
    float potency();
}

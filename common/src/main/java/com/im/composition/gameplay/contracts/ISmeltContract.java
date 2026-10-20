package com.im.composition.gameplay.contracts;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.effects.modifiers.IEffectModifier;

public interface ISmeltContract extends IEffectModifier
{
    void onSmelted(ICompositeBlockEntity furnace);
}
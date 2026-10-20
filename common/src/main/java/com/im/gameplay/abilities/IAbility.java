package com.im.gameplay.abilities;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;

public interface IAbility
{
    default void cast(ICompositeBlockEntity caster, int degree) { }
    default void tick(ICompositeBlockEntity caster, int degree, float normalizedTime) { }
}
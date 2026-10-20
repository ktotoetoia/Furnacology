package com.im.gameplay.effects.groups;

import com.im.core.lifecycle.typeregistry.ITypeRegistry;
import com.im.gameplay.effects.modifiers.IEffectModifier;

public interface IEffectGroup
{
    ITypeRegistry<IEffectModifier> getModifiers();
}

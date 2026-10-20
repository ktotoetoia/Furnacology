package com.im.gameplay.effects.groups;

import com.im.core.lifecycle.typeregistry.ITypeRegistry;
import com.im.core.lifecycle.typeregistry.TypeRegistry;
import com.im.gameplay.effects.modifiers.IEffectModifier;

import java.util.Collection;

public class EffectGroup implements IEffectGroup
{
    private final ITypeRegistry<IEffectModifier> _modifiers;

    public EffectGroup(Collection<? extends IEffectModifier> modifiers) { _modifiers = new TypeRegistry<>(modifiers); }

    public ITypeRegistry<IEffectModifier> getModifiers() { return _modifiers; }
}

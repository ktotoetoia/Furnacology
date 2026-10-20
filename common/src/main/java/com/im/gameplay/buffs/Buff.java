package com.im.gameplay.buffs;

import com.im.gameplay.abilities.IAbility;
import com.im.gameplay.abilities.ReleaseStyle;
import com.im.gameplay.effects.effectfactories.IEffectContext;
import com.im.gameplay.effects.effectfactories.IEffectModifierFactory;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public record Buff(List<IEffectModifierFactory> effects, @Nullable IAbility instant, @Nullable ReleaseStyle release)
{
    public Buff
    {
        if (instant != null && release == null) throw new IllegalArgumentException("An instant effect needs a release style");
    }

    public Buff(IEffectModifierFactory... effects) { this(List.of(effects), null, null); }

    public BuffEffectGroup create(ResourceLocation id, IEffectContext context, int duration, int remaining)
    {
        return new BuffEffectGroup(id, context.potency(), effects.stream().map(effect -> effect.create(context)).toList(), duration, remaining);
    }
}
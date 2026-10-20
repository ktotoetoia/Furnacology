package com.im.gameplay.buffs;

import com.im.gameplay.effects.groups.TemporaryEffectGroup;
import com.im.gameplay.effects.modifiers.IEffectModifier;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BuffEffectGroup extends TemporaryEffectGroup
{
    private final ResourceLocation _buff;
    private final float _potency;

    public BuffEffectGroup(ResourceLocation buff, float potency, List<IEffectModifier> modifiers, int duration, int remaining)
    {
        super(modifiers, duration, remaining);
        _buff = buff;
        _potency = potency;
    }

    public ActiveBuff toActiveBuff() { return new ActiveBuff(_buff, _potency, getDuration(), getRemaining()); }
}
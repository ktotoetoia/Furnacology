package com.im.composition.gameplay.effects;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.statemachine.statemachine.ITickable;
import com.im.gameplay.area.IArea;
import com.im.gameplay.effects.modifiers.IEffectModifier;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class AuraEffectModifier implements IEffectModifier, ITickable
{
    private static final int OverlapTicks = 20;

    private final ICompositeBlockEntity _target;
    private final Holder<MobEffect> _effect;
    private final Supplier<Integer> _amplifier;
    private final Supplier<Integer> _interval;

    public AuraEffectModifier(ICompositeBlockEntity target, Holder<MobEffect> effect, Supplier<Integer> amplifier, Supplier<Integer> interval)
    {
        _target = target;
        _effect = effect;
        _amplifier = amplifier;
        _interval = interval;
    }

    public void tick()
    {
        Level level = _target.getEntityLevel();
        int every = Math.max(1, _interval.get());

        if (level == null||level.getGameTime() % every !=  0L) return;

        _target.tryGet(IArea.class).ifPresent(area -> area.getEntities(LivingEntity.class)
                .forEach(entity -> entity.addEffect(new MobEffectInstance(_effect, every + OverlapTicks, _amplifier.get(), true, false))));
    }
}
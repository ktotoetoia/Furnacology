package com.im.gameplay.augments;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.effects.effectfactories.IEffectContext;
import com.im.gameplay.effects.effectfactories.IEffectModifierFactory;
import com.im.gameplay.effects.groups.EffectGroup;
import com.im.gameplay.effects.groups.IEffectGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public record Augment(Supplier<? extends ItemLike> item, ToIntFunction<ICompositeBlockEntity> maxStacks, List<IEffectModifierFactory> effects)
{
    public static Augment single(Supplier<? extends ItemLike> item, IEffectModifierFactory... effects) { return new Augment(item, entity -> 1, List.of(effects)); }

    public ItemStack toStack(int stacks) { return new ItemStack(item.get(), stacks); }
    public IEffectGroup create(IEffectContext context) { return new EffectGroup(effects.stream().map(effect -> effect.create(context)).toList()); }
}
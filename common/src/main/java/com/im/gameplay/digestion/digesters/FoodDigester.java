package com.im.gameplay.digestion.digesters;

import com.im.gameplay.buffs.BuffGrant;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public class FoodDigester implements IDigester<ItemStack>
{
    private final ResourceLocation _buff;
    private final Function<FoodProperties, Float> _potency;
    private final Function<FoodProperties, Integer> _duration;

    public FoodDigester(ResourceLocation buff, Function<FoodProperties, Float> potency, Function<FoodProperties, Integer> duration)
    {
        _buff = buff;
        _potency = potency;
        _duration = duration;
    }

    public List<BuffGrant> digest(ItemStack input)
    {
        FoodProperties food = input.get(DataComponents.FOOD);

        if (food == null) return List.of();

        float potency = _potency.apply(food);
        int ticks = _duration.apply(food);

        return potency > 0.0f && ticks > 0 ? List.of(new BuffGrant(_buff, ticks, potency)) : List.of();
    }
}

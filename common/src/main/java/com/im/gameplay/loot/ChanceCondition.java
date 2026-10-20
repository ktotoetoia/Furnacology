package com.im.gameplay.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ChanceCondition implements LootItemCondition
{
    private final ResourceLocation _chance;
    private final Function<ResourceLocation, LootChance> _chances;
    private final Supplier<LootItemConditionType> _type;

    private ChanceCondition(ResourceLocation chance, Function<ResourceLocation, LootChance> chances, Supplier<LootItemConditionType> type)
    {
        _chance = chance;
        _chances = chances;
        _type = type;
    }

    public static MapCodec<ChanceCondition> codec(Function<ResourceLocation, LootChance> chances, Supplier<LootItemConditionType> type)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("chance").forGetter(condition -> condition._chance)
        ).apply(instance, chance -> new ChanceCondition(chance, chances, type)));
    }

    public LootItemConditionType getType() { return _type.get(); }

    public boolean test(LootContext context)
    {
        LootChance odds = _chances.apply(_chance);

        if (odds ==  null) return false;

        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        int level = tool == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(context.getResolver().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(odds.enchantment()), tool);

        return context.getRandom().nextDouble() < odds.getChanceAt(level);
    }
}
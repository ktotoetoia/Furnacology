package com.im.gameplay.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;
import java.util.function.Supplier;

public final class BlockTagCondition implements LootItemCondition
{
    private final TagKey<Block> _tag;
    private final Supplier<LootItemConditionType> _type;

    private BlockTagCondition(TagKey<Block> tag, Supplier<LootItemConditionType> type)
    {
        _tag = tag;
        _type = type;
    }

    public static MapCodec<BlockTagCondition> codec(Supplier<LootItemConditionType> type)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(condition -> condition._tag)
        ).apply(instance, tag -> new BlockTagCondition(tag, type)));
    }

    public LootItemConditionType getType() { return _type.get(); }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() { return Set.of(LootContextParams.BLOCK_STATE); }

    public boolean test(LootContext context)
    {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        return state != null && state.is(_tag);
    }
}
package com.im.databases.loot;

import com.im.Furnacology;
import com.im.core.lifecycle.database.Database;
import com.im.gameplay.loot.BlockTagCondition;
import com.im.gameplay.loot.ChanceCondition;
import com.im.gameplay.loot.LootChance;
import com.im.gameplay.loot.LootInjector;
import com.im.infrastructure.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.function.Supplier;

import static com.im.infrastructure.config.FurnacologyConfig.PerfectCoalDropChance;
import static com.im.infrastructure.config.FurnacologyConfig.PerfectCoalFortuneBonus;

public final class LootDatabase
{
    public static final Registrar<LootItemConditionType> LootConditions = new Registrar<>(BuiltInRegistries.LOOT_CONDITION_TYPE, Furnacology.ModId);
    public static final Database<LootChance> Chances = new Database<>();
    public static final LootInjector Injector = new LootInjector();
    public static final ResourceLocation PerfectCoalChunkChance = Furnacology.id("perfect_coal_chunk");
    public static final ResourceKey<LootTable> PerfectCoalChunk = ResourceKey.create(Registries.LOOT_TABLE, Furnacology.id("gameplay/perfect_coal_chunk"));

    public static final Supplier<LootItemConditionType> BlockTag =LootConditions.register("block_tag",
            () -> new LootItemConditionType(BlockTagCondition.codec(() -> LootDatabase.BlockTag.get())));
    public static final Supplier<LootItemConditionType> Chance = LootConditions.register("chance",
            () -> new LootItemConditionType(ChanceCondition.codec(Chances::getById, () -> LootDatabase.Chance.get())));

    public static void register()
    {
        Chances.add(PerfectCoalChunkChance, new LootChance(PerfectCoalDropChance::get,Enchantments.FORTUNE, PerfectCoalFortuneBonus::get));
        Injector.add(LootInjector::isBlockTable, PerfectCoalChunk);
    }
}
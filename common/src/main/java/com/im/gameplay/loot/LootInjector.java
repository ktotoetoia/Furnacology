package com.im.gameplay.loot;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class LootInjector
{
    private static final String BlockTables = "blocks/";

    private final List<Injection> _injections = new ArrayList<>();
    public static boolean isBlockTable(ResourceKey<LootTable> table) { return table.location().getPath().startsWith(BlockTables); }

    public void add(Predicate<ResourceKey<LootTable>> targets, ResourceKey<LootTable> table) { _injections.add(new Injection(targets, table)); }

    public List<LootPool.Builder> getPoolsFor(ResourceKey<LootTable> target)
    {
        return _injections.stream().filter(injection -> injection.targets().test(target))
                .map(injection -> LootPool.lootPool().add(NestedLootTable.lootTableReference(injection.table()))).toList();
    }
    private record Injection(Predicate<ResourceKey<LootTable>> targets, ResourceKey<LootTable> table) { }
}
package com.im.gameplay.smelting.memory;

import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SmeltingMemory
{
    private final Map<Item, Integer> _smeltsByItem = new HashMap<>();

    public int getCount(Item item) { return _smeltsByItem.getOrDefault(item, 0); }
    public boolean isEmpty() { return _smeltsByItem.isEmpty(); }
    public Map<Item, Integer> getSnapshot() { return new LinkedHashMap<>(_smeltsByItem); }
    public SmeltingMemoryData toData() { return new SmeltingMemoryData(_smeltsByItem); }
    public void recordSmelt(Item item) { _smeltsByItem.merge(item, 1, Integer::sum); }
    public Tag save() { return SmeltingMemoryData.DataCodec.encodeStart(NbtOps.INSTANCE, toData()).getOrThrow(); }
    public void load(Tag tag) { SmeltingMemoryData.DataCodec.parse(NbtOps.INSTANCE, tag).resultOrPartial().ifPresent(this::load); }

    public void load(SmeltingMemoryData data)
    {
        _smeltsByItem.clear();
        _smeltsByItem.putAll(data.counts());
    }
}
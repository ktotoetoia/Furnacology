package com.im.core.lifecycle.database;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Database<T>
{
    private final Map<ResourceLocation, T> _entries = new ConcurrentHashMap<>();

    public Database<T> add(ResourceLocation id, T entry)
    {
        _entries.put(id, entry);
        return this;
    }

    @Nullable public T getById(ResourceLocation id) { return _entries.get(id); }
}
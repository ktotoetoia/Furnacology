package com.im.infrastructure.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class Registrar<T>
{
    private final Registry<T> _registry;
    private final String _namespace;
    private final List<Entry<? extends T>> _entries = new ArrayList<>();

    public Registrar(Registry<T> registry, String namespace)
    {
        _registry = registry;
        _namespace = namespace;
    }

    public List<Supplier<? extends T>> getEntries() { return List.copyOf(_entries); }
    public <TValue extends T> Supplier<TValue> register(String name, Supplier<TValue> factory) { return register(name, factory, true); }

    public <TValue extends T> Supplier<TValue> register(String name, Supplier<TValue> factory, boolean enabled)
    {
        Entry<TValue> entry = new Entry<>(ResourceLocation.fromNamespaceAndPath(_namespace, name), factory);

        if (enabled) _entries.add(entry);

        return entry;
    }

    public void registerAll()
    {
        for (Entry<? extends T> entry : _entries)
        {
            bind(entry);
        }
    }

    private <TValue extends T> void bind(Entry<TValue> entry) { entry._registered = Services.Platform.register(_registry, entry._id, entry._factory); }

    private static final class Entry<TValue> implements Supplier<TValue>
    {
        private final ResourceLocation _id;
        private final Supplier<TValue> _factory;
        @Nullable private Supplier<TValue> _registered;

        private Entry(ResourceLocation id, Supplier<TValue> factory)
        {
            _id = id;
            _factory = factory;
        }

        public TValue get()
        {
            if (_registered == null) throw new IllegalStateException(_id + " is not registered");

            return _registered.get();
        }
    }
}

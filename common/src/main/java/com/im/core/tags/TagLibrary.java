package com.im.core.tags;

import com.im.core.values.random.WeightedRandom;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TagLibrary<T> implements ITagLibrary<T>
{
    private final Map<Tag<T>, WeightedRandom<T>> _entries = new LinkedHashMap<>();
    @Nullable private ITagLibrary<T> _parent;

    public TagLibrary<T> add(Tag<T> tag, T value) { return add(tag, WeightedRandom.of(value)); }

    public TagLibrary<T> add(Tag<T> tag, WeightedRandom<T> values)
    {
        if (_entries.putIfAbsent(tag, values) != null) throw new IllegalArgumentException(tag.name() + " is already added");

        return this;
    }

    public TagLibrary<T> withParent(@Nullable ITagLibrary<T> parent)
    {
        _parent = parent;
        return this;
    }

    public List<T> getAll(Tag<T> tag)
    {
        WeightedRandom<T> found = find(tag);

        if (found != null) return found.getItems();

        return _parent == null ? List.of() : _parent.getAll(tag);
    }

    @Nullable
    public T getRandom(Tag<T> tag, RandomSource random)
    {
        WeightedRandom<T> found = find(tag);

        if (found != null) return found.next(random);

        return _parent == null ? null : _parent.getRandom(tag, random);
    }

    @Nullable
    private WeightedRandom<T> find(Tag<T> tag)
    {
        for (Tag<T> current = tag; current != null; current = current.fallback())
        {
            WeightedRandom<T> found = _entries.get(current);

            if (found != null) return found;
        }

        return null;
    }
}
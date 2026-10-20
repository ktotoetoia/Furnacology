package com.im.core.values.random;

import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class WeightedRandom<T>
{
    private static final Supplier<Double> Even = () -> 1.0;

    private final List<WeightedEntry<T>> _entries = new ArrayList<>();

    public static <T> WeightedRandom<T> of(T item) { return new WeightedRandom<T>().add(item); }

    public List<WeightedEntry<T>> getEntries() { return Collections.unmodifiableList(_entries); }
    public List<T> getItems() { return _entries.stream().map(WeightedEntry::item).toList(); }
    public boolean isEmpty() { return _entries.isEmpty(); }
    public WeightedRandom<T> add(T item) { return add(item, Even); }

    public WeightedRandom<T> add(T item, Supplier<Double> weight)
    {
        _entries.add(new WeightedEntry<>(item, weight));
        return this;
    }

    public T next(RandomSource random)
    {
        int index = nextIndex(random);
        return index < 0 ? null : _entries.get(index).item();
    }

    public int nextIndex(RandomSource random)
    {
        if (_entries.isEmpty()) return -1;

        double totalWeight = _entries.stream().mapToDouble(WeightedEntry::getChance).sum();

        if (totalWeight <=  0.0) return 0;

        double number = random.nextDouble() * totalWeight;

        for (int index = 0; index < _entries.size(); index++)
        {
            number -= _entries.get(index).getChance();

            if (number < 0.0) return index;
        }

        return _entries.size() - 1;
    }
}
package com.im.gameplay.digestion.digesters;

import com.im.gameplay.buffs.BuffGrant;

import java.util.List;
import java.util.function.Predicate;

public class FilteredDigester<T> implements IDigester<T>
{
    private final IDigester<T> _digester;
    private final Predicate<T> _filter;

    public FilteredDigester(IDigester<T> digester, Predicate<T> filter)
    {
        _digester = digester;
        _filter = filter;
    }

    public List<BuffGrant> digest(T input) { return _filter.test(input) ? _digester.digest(input) : List.of(); }
}

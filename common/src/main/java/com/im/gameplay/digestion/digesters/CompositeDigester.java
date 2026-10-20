package com.im.gameplay.digestion.digesters;

import com.im.gameplay.buffs.BuffGrant;

import java.util.List;

public class CompositeDigester<T> implements IDigester<T>
{
    private final List<IDigester<T>> _digesters;

    @SafeVarargs
    public CompositeDigester(IDigester<T>... digesters) { _digesters = List.of(digesters); }

    public List<BuffGrant> digest(T input) { return _digesters.stream().flatMap(digester -> digester.digest(input).stream()).toList(); }
}

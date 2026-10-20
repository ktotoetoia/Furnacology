package com.im.core.lifecycle.typeregistry;

import java.util.Collection;
import java.util.List;

public class TypeRegistry<TType> implements ITypeRegistry<TType>
{
    private final List<TType> _collection;

    public TypeRegistry(Collection<? extends TType> collection) { _collection = List.copyOf(collection); }
    public Collection<TType> getCollection() { return _collection; }
}
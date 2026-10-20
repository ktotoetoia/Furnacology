package com.im.core.lifecycle.typeregistry;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ITypeRegistry<TType>
{
    Collection<TType> getCollection();

    default <T> T get(Class<T> type) { return tryGet(type).orElse(null); }
    default <T> Optional<T> tryGet(Class<T> type) { return getCollection().stream().filter(type::isInstance).map(type::cast).findFirst(); }
    default <T> List<T> getAll(Class<T> type) { return getCollection().stream().filter(type::isInstance).map(type::cast).toList(); }
    default boolean hasOfType(Class<?> type) { return getCollection().stream().anyMatch(type::isInstance); }
}
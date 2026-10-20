package com.im.gameplay.effects.containers;

import com.im.gameplay.effects.groups.IEffectGroup;

import java.util.List;

public interface IEffectContainer
{
    List<IEffectGroup> getGroups();
    void addGroup(IEffectGroup group);
    void removeGroup(IEffectGroup group);

    default <T> List<T> getGroups(Class<T> type) { return getGroups().stream().filter(type::isInstance).map(type::cast).toList(); }
    default <T> List<T> getModifiers(Class<T> type) { return getGroups().stream().flatMap(group -> group.getModifiers().getAll(type).stream()).toList(); }
    default boolean hasModifier(Class<?> type) { return getGroups().stream().anyMatch(group -> group.getModifiers().hasOfType(type)); }
}

package com.im.gameplay.effects.containers;

import com.im.gameplay.effects.groups.IEffectGroup;
import com.im.gameplay.effects.groups.ITemporaryEffectGroup;
import com.im.gameplay.effects.groups.IUpdatableEffectGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EffectContainer implements IEffectContainer
{
    private final List<IEffectObserver> _observers = new ArrayList<>();
    private final List<IEffectGroup> _groups = new ArrayList<>();

    public List<IEffectGroup> getGroups() { return Collections.unmodifiableList(_groups); }
    public void addObserver(IEffectObserver observer) { _observers.add(observer); }

    public void addGroup(IEffectGroup group)
    {
        if (_groups.contains(group)) return;

        _groups.add(group);
        _observers.forEach(observer -> observer.onEffectGroupAdded(group));
    }

    public void removeGroup(IEffectGroup group)
    {
        if (!_groups.remove(group)) return;

        _observers.forEach(observer -> observer.onEffectGroupRemoved(group));
    }

    public void update()
    {
        for (IEffectGroup group : List.copyOf(_groups))
        {
            if (group instanceof IUpdatableEffectGroup updatable) updatable.update();
            if (group instanceof ITemporaryEffectGroup temporary && temporary.isFinished()) removeGroup(group);
        }
    }
}

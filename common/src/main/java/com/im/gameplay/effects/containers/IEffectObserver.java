package com.im.gameplay.effects.containers;

import com.im.gameplay.effects.groups.IEffectGroup;

public interface IEffectObserver
{
    void onEffectGroupAdded(IEffectGroup group);
    void onEffectGroupRemoved(IEffectGroup group);
}

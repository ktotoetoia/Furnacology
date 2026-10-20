package com.im.composition.gameplay.effects;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.effects.containers.IEffectObserver;
import com.im.gameplay.effects.groups.IEffectGroup;

public class RequestSyncOnEffectChanged implements IEffectObserver
{
    private final ICompositeBlockEntity _entity;

    public RequestSyncOnEffectChanged(ICompositeBlockEntity entity) { _entity = entity; }
    public void onEffectGroupAdded(IEffectGroup group) { _entity.requestSync(); }
    public void onEffectGroupRemoved(IEffectGroup group) { _entity.requestSync(); }
}

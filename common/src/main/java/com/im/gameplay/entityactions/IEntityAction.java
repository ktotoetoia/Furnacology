package com.im.gameplay.entityactions;

import com.im.core.statemachine.statemachine.ITickable;

public interface IEntityAction extends ITickable
{
    default void onEnter() { }
    default void onExit() { }
    @Override default void tick() { }

    static IEntityAction runOnEnter(Runnable action)
    {
        return new IEntityAction()
        {
            @Override public void onEnter() { action.run(); }
        };
    }
}

package com.im.core.statemachine.statemachine;

import com.im.core.statemachine.states.IState;

public interface IStateMachine extends ITickable
{
    IState getCurrentState();
    void updateTransition();
}
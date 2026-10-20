package com.im.core.statemachine.transition;

import com.im.core.statemachine.states.IState;

public interface ITransition
{
    IState from();
    IState to();
    boolean canTransition();
}
package com.im.core.statemachine.states;

import com.im.core.statemachine.transition.ITransition;

import java.util.stream.Stream;

public interface IState
{
    void onEnter();
    void onExit();
    void addTransition(ITransition transition);
    Stream<ITransition> getAvailableTransitions();
}
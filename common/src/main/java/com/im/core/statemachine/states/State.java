package com.im.core.statemachine.states;

import com.im.core.statemachine.statemachine.ITickable;
import com.im.core.statemachine.transition.ITransition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class State implements IState, ITickable
{
    protected final List<ITransition> _transitions = new ArrayList<>();

    public void onEnter() { }
    public void onExit() { }
    public void tick() { }
    public void addTransition(ITransition transition) { _transitions.add(transition); }
    public Stream<ITransition> getAvailableTransitions() { return _transitions.stream().filter(ITransition::canTransition); }
}
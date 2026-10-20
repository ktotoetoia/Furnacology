package com.im.core.statemachine.statemachine;

import com.im.core.statemachine.states.IState;
import com.im.core.statemachine.transition.ITransition;

public class StateMachine implements IStateMachine,ITickable
{
    private IState _currentState;

    public StateMachine(IState startState)
    {
        _currentState = startState;
        startState.onEnter();
    }

    public IState getCurrentState() { return _currentState; }


    public void tick()
    {
        if (_currentState instanceof ITickable tickable) tickable.tick();
        updateTransition();
    }

    public void updateTransition()
    {
        _currentState.getAvailableTransitions().findFirst().map(ITransition::to).ifPresent(this::setCurrentState);
    }

    private void setCurrentState(IState state)
    {
        if (_currentState == state) return;

        _currentState.onExit();
        _currentState = state;
        state.onEnter();
    }
}
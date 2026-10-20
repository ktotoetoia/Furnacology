package com.im.core.statemachine.transition;

import com.im.core.statemachine.states.IState;

import java.util.function.BooleanSupplier;

public record Transition(IState from, IState to, BooleanSupplier condition) implements ITransition
{
    public boolean canTransition() { return condition.getAsBoolean(); }
}
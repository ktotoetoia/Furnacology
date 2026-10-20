package com.im.gameplay.entityactions;

import com.im.core.statemachine.states.State;

import java.util.List;

public class ActionState extends State
{
    private final List<IEntityAction> _actions;
    public ActionState(IEntityAction... actions) { _actions = List.of(actions); }

    @Override
    public void onEnter() { _actions.forEach(IEntityAction::onEnter); }
    @Override
    public void onExit() { _actions.forEach(IEntityAction::onExit); }
    @Override
    public void tick() { _actions.forEach(IEntityAction::tick); }
}
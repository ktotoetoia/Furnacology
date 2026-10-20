package com.im.gameplay.effects.groups;

public interface ITemporaryEffectGroup extends IEffectGroup
{
    int getDuration();
    int getRemaining();
    boolean isFinished();
}

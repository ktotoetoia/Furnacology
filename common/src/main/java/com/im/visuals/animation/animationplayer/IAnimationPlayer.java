package com.im.visuals.animation.animationplayer;

import com.im.core.tags.Tag;
import com.im.visuals.animation.clips.Clip;

public interface IAnimationPlayer
{
    void play(Tag<Clip> tag, boolean loop);
    void advance(float speed, boolean running);
    void setVisible(boolean visible);
    void stop();
    int getFrameIndex();
    boolean isFinished();
}
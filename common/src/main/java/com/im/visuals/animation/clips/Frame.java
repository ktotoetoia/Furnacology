package com.im.visuals.animation.clips;

public record Frame(Strip strip, int index, float ticks)
{
    public Frame
    {
        if (index < 0 || index >= strip.frameCount()) throw new IllegalArgumentException("Frame " + index + " is outside its strip");
        if (ticks <= 0.0f) throw new IllegalArgumentException("A frame needs to last a positive number of ticks");
    }

    public float getHeight() { return 1.0f / strip.frameCount(); }
    public float getOffset() { return index * getHeight(); }
}
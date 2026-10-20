package com.im.visuals.animation.clips;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.IntStream;

public final class Clip
{
    private final List<Frame> _frames;
    private final float[] _ends;

    public Clip(List<Frame> frames)
    {
        if (frames.isEmpty()) throw new IllegalArgumentException("Clip needs one or more frames");

        _frames = List.copyOf(frames);
        _ends = new float[frames.size()];
        float time = 0.0f;

        for (int index = 0; index < _ends.length; index++)
        {
            time += frames.get(index).ticks();
            _ends[index] = time;
        }
    }
    public static Clip of(ResourceLocation texture, int frameCount, float ticksPerFrame)
    {
        Strip strip = new Strip(texture, frameCount);
        return new Clip(IntStream.range(0, strip.frameCount()).mapToObj(index -> new Frame(strip, index, ticksPerFrame)).toList());
    }
    public int getFrameCount() { return _frames.size(); }
    public float getLength() { return _ends[_ends.length - 1]; }
    public float getEndOf(int index) { return _ends[index]; }
    public Frame getFrameAt(float time) { return _frames.get(getIndexAt(time)); }
    public int getIndexAt(float time)
    {
        for (int index = 0; index < _ends.length; index++)
        {
            if (time < _ends[index]) return index;
        }

        return _ends.length - 1;
    }
}
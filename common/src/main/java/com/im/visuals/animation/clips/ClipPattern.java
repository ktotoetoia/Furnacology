package com.im.visuals.animation.clips;

import java.util.function.Function;
import java.util.function.Supplier;

public record ClipPattern(String prefix, Function<Integer, Float> ticksPerFrame, Supplier<Double> weight)
{
    private static final Supplier<Double> Even = () -> 1.0;

    public static ClipPattern of(String prefix, float ticksPerFrame) { return new ClipPattern(prefix, frames -> ticksPerFrame, Even); }
    public static ClipPattern lasting(String prefix, float ticks) { return new ClipPattern(prefix, frames -> ticks / frames, Even); }
    public ClipPattern withWeight(Supplier<Double> weight) { return new ClipPattern(prefix, ticksPerFrame, weight); }
}
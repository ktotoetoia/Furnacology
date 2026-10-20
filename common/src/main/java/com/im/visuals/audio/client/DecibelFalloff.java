package com.im.visuals.audio.client;

import java.util.function.Supplier;

public class DecibelFalloff implements IVolumeFalloff
{
    private final Supplier<Double> _range;
    private final Supplier<Double> _decibelsPerBlock;
    private final double _fullVolumeDistance;
    private final double _taperFraction;

    public DecibelFalloff(Supplier<Double> range, Supplier<Double> decibelsPerBlock, double fullVolumeDistance, double taperFraction)
    {
        _range = range;
        _decibelsPerBlock = decibelsPerBlock;
        _fullVolumeDistance = fullVolumeDistance;
        _taperFraction = taperFraction;
    }

    public float getVolumeAt(double distance)
    {
        double reach = _range.get();

        if (distance >= reach) return 0.0f;

        double decibels = -_decibelsPerBlock.get() * Math.max(0.0, distance - _fullVolumeDistance);
        double taperStart = reach * (1.0 - _taperFraction);
        double taper = distance <= taperStart ? 1.0 :(reach - distance) / (reach - taperStart);

        //10^(dB/20)
        return (float) (Math.pow(10.0,decibels/20.0)*taper);
    }
}

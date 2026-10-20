package com.im.gameplay.smelting.memory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record SpeedCurve(List<Step> steps)
{
    public static final List<String> DefaultEntries = List.of("0:0.75", "8:1.00", "16:1.25", "32:1.50","64:1.75","64:2.00","96:2.25","96:2.50","128:2.50","128:2.75","192:3.00");
    public static final SpeedCurve Default = parse(DefaultEntries);
    private static final float UntrainedSpeed = 1.0f;

    private static final Codec<Step> StepCodec = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("from").forGetter(Step::from),
            Codec.FLOAT.fieldOf("speed").forGetter(Step::speed)
    ).apply(instance, Step::new));

    public static final Codec<SpeedCurve> DataCodec = StepCodec.listOf().xmap(SpeedCurve::new, SpeedCurve::steps);

    public float getSpeedFor(int smelts)
    {
        float speed = UntrainedSpeed;

        for (Step step : steps)
        {
            if (smelts < step.from()) break;
            speed = step.speed();
        }

        return speed;
    }

    public static SpeedCurve parse(List<? extends String> entries)
    {
        List<Step> steps = new ArrayList<>();
        int total = 0;

        for (String entry : entries)
        {
            Step increment = parseEntry(entry);
            if (increment == null) continue;

            total += increment.from();
            steps.add(new Step(total, increment.speed()));
        }

        return new SpeedCurve(steps);
    }

    public static boolean isValidEntry(Object entry) { return entry instanceof String text && parseEntry(text) != null; }

    private static Step parseEntry(String entry)
    {
        String[] parts = entry.split(":");

        if (parts.length != 2) return null;

        try
        {
            int smelts = Integer.parseInt(parts[0].trim());
            float speed = Float.parseFloat(parts[1].trim());

            return smelts >= 0 && speed > 0.0f && Float.isFinite(speed) ? new Step(smelts, speed) : null;
        }
        catch (NumberFormatException exception)
        {
            return null;
        }
    }

    public record Step(int from, float speed) { }
}
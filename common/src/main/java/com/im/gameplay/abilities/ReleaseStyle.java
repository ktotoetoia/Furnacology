package com.im.gameplay.abilities;

import com.im.gameplay.abilities.blows.Blow;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public record ReleaseStyle(Supplier<Integer> duration, List<Blow> blows, @Nullable ResourceLocation icon)
{
    public ReleaseStyle
    {
        if (blows.isEmpty()) throw new IllegalArgumentException();
    }

    public ReleaseStyle(Supplier<Integer> duration, Blow blow) { this(duration, List.of(blow), null); }
}
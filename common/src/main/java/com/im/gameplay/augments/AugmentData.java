package com.im.gameplay.augments;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record AugmentData(Map<ResourceLocation, Integer> counts)
{
    public static final Codec<AugmentData> DataCodec = Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).xmap(AugmentData::new, AugmentData::counts);

    public AugmentData
    {
        counts = Map.copyOf(counts);
    }
}
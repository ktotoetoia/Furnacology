package com.im.gameplay.buffs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ActiveBuff(ResourceLocation buff,float potency, int duration, int remaining)
{
    public static final Codec<List<ActiveBuff>> ListCodec = RecordCodecBuilder.<ActiveBuff>create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("buff").forGetter(ActiveBuff::buff),
            Codec.FLOAT.fieldOf("potency").forGetter(ActiveBuff::potency),
            Codec.INT.fieldOf("duration").forGetter(ActiveBuff::duration),
            Codec.INT.fieldOf("remaining").forGetter(ActiveBuff::remaining)
    ).apply(instance, ActiveBuff::new)).listOf();
}
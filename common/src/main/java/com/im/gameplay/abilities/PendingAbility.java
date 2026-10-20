package com.im.gameplay.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record PendingAbility(ResourceLocation buff, Optional<ResourceLocation> icon)
{
    public static final Codec<List<PendingAbility>> ListCodec = RecordCodecBuilder.<PendingAbility>create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("buff").forGetter(PendingAbility::buff),
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(PendingAbility::icon)
    ).apply(instance, PendingAbility::new)).listOf();
}
package com.im.gameplay.buffs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record BuffGrant(ResourceLocation buff, int duration, float potency, Optional<ResourceLocation> icon)
{
    public static final int DefaultDuration = 1200;

    public static final Codec<BuffGrant> DataCodec = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("buff").forGetter(BuffGrant::buff),
            Codec.INT.optionalFieldOf("duration", DefaultDuration).forGetter(BuffGrant::duration),
            Codec.FLOAT.optionalFieldOf("potency", 1.0f).forGetter(BuffGrant::potency),
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(BuffGrant::icon)
    ).apply(instance, BuffGrant::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BuffGrant> NetworkCodec = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, BuffGrant::buff,
            ByteBufCodecs.VAR_INT, BuffGrant::duration,
            ByteBufCodecs.FLOAT, BuffGrant::potency,
            ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), BuffGrant::icon,
            BuffGrant::new);

    public static final Codec<List<BuffGrant>> ListCodec = Codec.withAlternative(DataCodec.listOf(), DataCodec, List::of);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<BuffGrant>> ListNetworkCodec = NetworkCodec.apply(ByteBufCodecs.list());

    public BuffGrant
    {
        duration = Math.max(1, duration);
    }

    public BuffGrant(ResourceLocation buff, int duration) { this(buff, duration, 1.0f, Optional.empty()); }
    public BuffGrant(ResourceLocation buff, int duration, float potency) { this(buff, duration, potency, Optional.empty()); }
}
package com.im.visuals.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class TimedParticleType extends ParticleType<TimedParticleOptions>
{
    private final MapCodec<TimedParticleOptions> _codec =
            Codec.INT.fieldOf("lifetime").xmap(lifetime -> new TimedParticleOptions(this, lifetime), TimedParticleOptions::lifetime);
    private final StreamCodec<ByteBuf, TimedParticleOptions> _streamCodec =
            ByteBufCodecs.VAR_INT.map(lifetime -> new TimedParticleOptions(this, lifetime), TimedParticleOptions::lifetime);

    public TimedParticleType() { super(false); }

    @Override
    public MapCodec<TimedParticleOptions> codec() { return _codec; }
    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, TimedParticleOptions> streamCodec() { return _streamCodec; }
}
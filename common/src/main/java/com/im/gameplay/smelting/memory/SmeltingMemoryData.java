package com.im.gameplay.smelting.memory;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public record SmeltingMemoryData(Map<Item, Integer> counts)
{
    public static final Codec<SmeltingMemoryData> DataCodec = Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT)
            .xmap(SmeltingMemoryData::new, SmeltingMemoryData::counts);
    public static final StreamCodec<RegistryFriendlyByteBuf, SmeltingMemoryData> NetworkCodec =
            ByteBufCodecs.<RegistryFriendlyByteBuf, Item, Integer, HashMap<Item, Integer>>map(HashMap::new, ByteBufCodecs.registry(Registries.ITEM), ByteBufCodecs.VAR_INT)
                    .map(SmeltingMemoryData::new, data -> new HashMap<>(data.counts()));

    public SmeltingMemoryData
    {
        counts = Map.copyOf(counts);
    }
}
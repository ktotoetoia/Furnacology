package com.im.databases.registries;

import com.im.Furnacology;
import com.im.gameplay.buffs.BuffGrant;
import com.im.gameplay.smelting.memory.SmeltingMemoryData;
import com.im.infrastructure.platform.Registrar;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class DataComponentRegistry
{
    public static final Registrar<DataComponentType<?>> Components = new Registrar<>(BuiltInRegistries.DATA_COMPONENT_TYPE, Furnacology.ModId);

    public static final Supplier<DataComponentType<SmeltingMemoryData>> SmeltingMemory = register("smelting_memory",
            builder -> builder.persistent(SmeltingMemoryData.DataCodec).networkSynchronized(SmeltingMemoryData.NetworkCodec));
    public static final Supplier<DataComponentType<List<BuffGrant>>> BuffGrants = register("buff",
            builder -> builder.persistent(BuffGrant.ListCodec).networkSynchronized(BuffGrant.ListNetworkCodec));
    public static final Supplier<DataComponentType<Unit>> ReplacesFoodBuff = register("replaces_food_buff",
            builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder)
    {
        return Components.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
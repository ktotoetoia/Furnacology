package com.im.core.lifecycle.blockentities;

import com.im.core.statemachine.statemachine.ITickable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.Supplier;

public class BlockPropertySetter<T extends Comparable<T>> implements ITickable
{
    private final ComponentBlockEntity _entity;
    private final Property<T> _property;
    private final Supplier<T> _value;

    public BlockPropertySetter(ComponentBlockEntity entity, Property<T> property, Supplier<T> value)
    {
        _entity = entity;
        _property = property;
        _value = value;
    }

    public void tick()
    {
        BlockState state = _entity.getBlockState();
        T wanted = _value.get();

        if (!_entity.isServer() || !state.hasProperty(_property) || state.getValue(_property).equals(wanted)) return;

        _entity.getLevel().setBlock(_entity.getBlockPos(), state.setValue(_property, wanted), Block.UPDATE_ALL);
        _entity.requestSync();
    }
}

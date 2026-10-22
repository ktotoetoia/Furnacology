package com.im.gameplay.area;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.values.scaling.IScalingValue;
import com.im.core.values.scaling.ScalingValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Area implements IArea
{
    private final ICompositeBlockEntity _entity;
    private final ScalingValue _radius;

    public Area(ICompositeBlockEntity entity, Supplier<Float> radius)
    {
        _entity = entity;
        _radius = new ScalingValue(radius);
    }

    public IScalingValue getRadius() { return _radius; }
    public AABB getBounds() { return new AABB(_entity.getEntityPos()).inflate(_radius.getFinalValue()); }

    public <T extends Entity> List<T> getEntities(Class<T> type, Predicate<? super T> filter)
    {
        Level level = _entity.getEntityLevel();
        return level == null ? List.of() : level.getEntitiesOfClass(type, getBounds(), filter);
    }
}
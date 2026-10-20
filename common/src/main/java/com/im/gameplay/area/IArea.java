package com.im.gameplay.area;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public interface IArea
{
    AABB getBounds();
    <T extends Entity> List<T> getEntities(Class<T> type, Predicate<? super T> filter);
    default <T extends Entity> List<T> getEntities(Class<T> type) { return getEntities(type, entity -> true); }
}
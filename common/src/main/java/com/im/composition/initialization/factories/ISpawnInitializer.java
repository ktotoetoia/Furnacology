package com.im.composition.initialization.factories;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.core.lifecycle.typeregistry.ITypeRegistry;

import java.util.List;

//remove augments to a separate project
public interface ISpawnInitializer
{
    List<Object> initialize(ComponentBlockEntity entity, ITypeRegistry<Object> parts);
}
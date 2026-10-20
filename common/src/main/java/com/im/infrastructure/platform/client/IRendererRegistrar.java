package com.im.infrastructure.platform.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface IRendererRegistrar
{
    <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> provider);
}

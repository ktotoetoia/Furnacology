package com.im.core.lifecycle.blockentities;

import com.im.infrastructure.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class ComponentEntityBlock extends BaseEntityBlock
{
    private final Supplier<? extends BlockEntityType<? extends ComponentBlockEntity>> _type;

    protected ComponentEntityBlock(Properties properties, Supplier<? extends BlockEntityType<? extends ComponentBlockEntity>> type)
    {
        super(properties);
        _type = type;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return _type.get().create(pos, state); }

    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return (tickLevel, pos, tickState, entity) ->
        {
            if (entity instanceof ComponentBlockEntity component) component.tick();
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (!(level.getBlockEntity(pos) instanceof ComponentBlockEntity entity) || !entity.opensMenu()) return InteractionResult.PASS;
        if (player instanceof ServerPlayer serverPlayer) Services.Platform.openMenu(serverPlayer, entity, pos);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ComponentBlockEntity entity)
        {
            entity.getAll(IHaveDrops.class).forEach(drops -> drops.getDrops().forEach(entity::dropItem));
            if (level instanceof ServerLevel server) entity.getAll(IRemovedObserver.class).forEach(observer -> observer.onRemoved(server));
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
package com.im.gameplay.smelting.furnace;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.core.lifecycle.blockentities.ComponentEntityBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.Supplier;

public class LivingFurnaceBlock extends ComponentEntityBlock
{
    public static final DirectionProperty Facing = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty Lit = BlockStateProperties.LIT;
    public static final int Skins = 3;
    public static final IntegerProperty Skin = IntegerProperty.create("skin", 0, Skins - 1);
    private static final float CrackleChance = 0.1f;
    private final MapCodec<LivingFurnaceBlock> _codec;

    public LivingFurnaceBlock(Properties properties,Supplier<BlockEntityType<ComponentBlockEntity>> type)
    {
        super(properties, type);
        _codec = simpleCodec(copy -> new LivingFurnaceBlock(copy, type));
        registerDefaultState(defaultBlockState().setValue(Facing, Direction.NORTH).setValue(Lit, false).setValue(Skin, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(Facing, Lit, Skin); }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return _codec; }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(Facing, context.getHorizontalDirection().getOpposite()); }
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(Facing, rotation.rotate(state.getValue(Facing))); }
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) { return rotate(state, mirror.getRotation(state.getValue(Facing))); }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (!state.getValue(Lit)) return;
        if (random.nextFloat() < CrackleChance) level.playLocalSound(pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, false);

        Direction facing = state.getValue(Facing);
        boolean alongX = facing.getAxis() == Direction.Axis.X;
        double drift = random.nextDouble() * 0.6 - 0.3;
        double x = pos.getX() + 0.5 + (alongX ? facing.getStepX() * 0.52 : drift);
        double y = pos.getY() + random.nextDouble() * 6.0 / 16.0;
        double z = pos.getZ() + 0.5 + (alongX ? drift : facing.getStepZ() * 0.52);

        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
    }
}
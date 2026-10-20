package com.im.gameplay.items;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TransformItem extends ColoredNameItem implements IUseFirstItem
{
    private static final int ParticleCount = 20;
    private static final double ParticleSpread = 0.4;
    private static final double ParticleSpeed = 0.02;

    private final Predicate<BlockState> _target;
    private final Supplier<? extends Block> _result;
    private final Supplier<SoundEvent> _sound;
    private final ParticleOptions _particle;

    public TransformItem(Properties properties, Predicate<BlockState> target, Supplier<? extends Block> result, Supplier<SoundEvent> sound, ParticleOptions particle,
                         ChatFormatting color)
    {
        super(properties, color);
        _target = target;
        _result = result;
        _sound = sound;
        _particle = particle;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) { ItemDescription.append(getDescriptionId(), tooltip); }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!_target.test(state)) return InteractionResult.PASS;

        if (level instanceof ServerLevel server) transform(server, pos, state, stack, context.getPlayer());

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void transform(ServerLevel level, BlockPos pos, BlockState state, ItemStack stack,Player player)
    {
        List<ItemStack> contents = takeContents(level.getBlockEntity(pos));
        Vec3 center = Vec3.atCenterOf(pos);

        level.setBlockAndUpdate(pos, copyProperties(state, _result.get().defaultBlockState()));
        giveContents(level, pos, contents);
        stack.consume(1, player);

        level.playSound(null, pos, _sound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        level.sendParticles(_particle, center.x, center.y, center.z, ParticleCount, ParticleSpread, ParticleSpread, ParticleSpread, ParticleSpeed);
    }

    private static List<ItemStack> takeContents(BlockEntity entity)
    {
        List<ItemStack> contents = new ArrayList<>();

        if (!(entity instanceof Container container)) return contents;

        for (int slot = 0; slot < container.getContainerSize(); slot++)
        {
            contents.add(container.removeItemNoUpdate(slot));
        }

        return contents;
    }

    private static void giveContents(ServerLevel level, BlockPos pos, List<ItemStack> contents)
    {
        Container items = level.getBlockEntity(pos) instanceof ComponentBlockEntity entity ? entity.getAccess() : null;

        for (int slot = 0; slot < contents.size(); slot++)
        {
            ItemStack stack = contents.get(slot);

            if (items != null && slot < items.getContainerSize() && items.getItem(slot).isEmpty() && items.canPlaceItem(slot, stack)) items.setItem(slot, stack);
            else Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }

    private static BlockState copyProperties(BlockState from, BlockState to)
    {
        for (Property<?> property : from.getProperties())
        {
            if (to.hasProperty(property)) to = copy(from, to, property);
        }

        return to;
    }

    private static <T extends Comparable<T>> BlockState copy(BlockState from, BlockState to, Property<T> property) { return to.setValue(property, from.getValue(property)); }
}

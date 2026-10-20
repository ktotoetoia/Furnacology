package com.im.gameplay.items;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.gameplay.augments.AugmentContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class AugmentItem extends ColoredNameItem implements IUseFirstItem
{
    private static  final String Added = "message.furnacology.upgrade.added";
    private static  final String Rejected = "message.furnacology.upgrade.rejected";
    private static  final String Hint = "item.furnacology.upgrade.hint";
    private final ResourceLocation _augment;
    private final Supplier<SoundEvent> _sound;

    public AugmentItem(Properties properties, ResourceLocation augment,Supplier<SoundEvent> sound, ChatFormatting color)
    {
        super(properties, color);
        _augment = augment;
        _sound = sound;
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        ItemDescription.append(getDescriptionId(), tooltip);
        tooltip.add(Component.translatable(Hint).withStyle(ChatFormatting.DARK_GRAY));
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        AugmentContainer augments = level.getBlockEntity(pos) instanceof ComponentBlockEntity entity ? entity.get(AugmentContainer.class) : null;
        Player player = context.getPlayer();

        if (augments == null) return InteractionResult.PASS;

        if (!augments.canApply(_augment))
        {
            if (!level.isClientSide()) tell(player, Rejected, stack.getHoverName(), augments);

            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) return InteractionResult.sidedSuccess(true);

        augments.apply(_augment);
        tell(player, Added, stack.getHoverName(), augments);
        level.playSound(null, pos, _sound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        stack.consume(1, player);
        return InteractionResult.sidedSuccess(false);
    }

    private void tell(@Nullable Player player, String message, Component name, AugmentContainer augments)
    {
        if (player != null) player.displayClientMessage(Component.translatable(message, name, augments.getCount(_augment), augments.getMaxStacks(_augment)), true);
    }
}
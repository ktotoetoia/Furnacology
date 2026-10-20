package com.im.gameplay.items;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.gameplay.augments.AugmentContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class CoalScrubberItem extends DescribedItem implements IUseFirstItem
{
    private static final String Removed = "message.furnacology.upgrade.removed";
    private static final String None = "message.furnacology.upgrade.none";
    private final Supplier<SoundEvent> _sound;

    public CoalScrubberItem(Properties properties, Supplier<SoundEvent> sound)
    {
        super(properties);
        _sound = sound;
    }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ComponentBlockEntity entity = level.getBlockEntity(pos) instanceof ComponentBlockEntity found ? found : null;
        AugmentContainer augments = entity == null ? null : entity.get(AugmentContainer.class);
        Player player = context.getPlayer();

        if (augments == null) return InteractionResult.PASS;
        if (augments.isEmpty())
        {
            if (!level.isClientSide() &&  player != null) player.displayClientMessage(Component.translatable(None), true);

            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) return InteractionResult.sidedSuccess(true);
        int removed = augments.getTotal();

        for (ItemStack augment :  augments.removeAll())
        {
            if (player != null) player.getInventory().placeItemBackInInventory(augment);
            else entity.dropItem(augment);
        }

        level.playSound(null, pos, _sound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player == null) return InteractionResult.sidedSuccess(false);

        player.displayClientMessage(Component.translatable(Removed, removed), true);
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
        return InteractionResult.sidedSuccess(false);
    }
}

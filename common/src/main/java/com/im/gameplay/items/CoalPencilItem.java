package com.im.gameplay.items;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.gameplay.skins.ISkin;
import com.im.gameplay.skins.SkinSelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CoalPencilItem extends DescribedItem implements IUseFirstItem
{
    private static final String Chosen = "message.furnacology.skin.chosen";
    private static final String Automatic = "message.furnacology.skin.automatic";
    private static final String SkinKey = "skin";

    public CoalPencilItem(Properties properties) { super(properties); }

    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
    {
        Level level = context.getLevel();
        SkinSelector<?> skins = level.getBlockEntity(context.getClickedPos()) instanceof ComponentBlockEntity entity ? entity.get(SkinSelector.class) : null;

        if (skins == null) return InteractionResult.PASS;

        if (!level.isClientSide())
        {
            ISkin skin = skins.cycle();
            Player player = context.getPlayer();
            Component name = Component.translatable(skin.id().toLanguageKey(SkinKey));

            if (player != null)
                player.displayClientMessage(Component.translatable(skins.isAutomatic() ? Automatic : Chosen, name), true);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
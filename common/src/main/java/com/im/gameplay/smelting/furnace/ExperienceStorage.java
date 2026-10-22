package com.im.gameplay.smelting.furnace;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.lifecycle.blockentities.IRemovedObserver;
import com.im.infrastructure.saving.IStateSerializable;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperienceStorage implements IStateSerializable, IRemovedObserver
{
    private static final String TagRecipesUsed = "recipes_used";
    private static final Codec<Map<ResourceLocation, Integer>> RecipesCodec = Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT);

    private final ICompositeBlockEntity _entity;
    private final Map<ResourceLocation, Integer> _recipesUsed = new HashMap<>();

    public ExperienceStorage(ICompositeBlockEntity entity) { _entity = entity; }

    public void onRemoved(ServerLevel level) { pop(level, Vec3.atCenterOf(_entity.getEntityPos())); }
    public void captureState(CompoundTag tag, HolderLookup.Provider registries) { tag.put(TagRecipesUsed, RecipesCodec.encodeStart(NbtOps.INSTANCE, _recipesUsed).getOrThrow()); }

    public void record(RecipeHolder<?> recipe)
    {
        _recipesUsed.merge(recipe.id(), 1, Integer::sum);
        _entity.markEntityChanged();
    }

    public void award(Player player)
    {
        if (player instanceof ServerPlayer server) server.awardRecipes(pop(server.serverLevel(), server.position()));
    }

    public void restoreState(CompoundTag tag, HolderLookup.Provider registries)
    {
        _recipesUsed.clear();
        RecipesCodec.parse(NbtOps.INSTANCE, tag.getCompound(TagRecipesUsed)).resultOrPartial().ifPresent(_recipesUsed::putAll);
    }

    private List<RecipeHolder<?>> pop(ServerLevel level, Vec3 position)
    {
        if (_recipesUsed.isEmpty()) return List.of();

        List<RecipeHolder<?>> recipes = new ArrayList<>();

        _recipesUsed.forEach((id, smelts) -> level.getRecipeManager().byKey(id).ifPresent(recipe ->
        {
            recipes.add(recipe);

            if (recipe.value() instanceof AbstractCookingRecipe cooking) spawnOrbs(level, position, smelts, cooking.getExperience());
        }));

        _recipesUsed.clear();
        _entity.markEntityChanged();
        return recipes;
    }

    private static void spawnOrbs(ServerLevel level, Vec3 position, int smelts, float experience)
    {
        float total = smelts * experience;
        int whole = Mth.floor(total);

        if (level.getRandom().nextFloat() < Mth.frac(total)) whole++;

        ExperienceOrb.award(level, position, whole);
    }
}

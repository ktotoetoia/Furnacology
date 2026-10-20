package com.im.databases.registries;

import com.im.Furnacology;
import com.im.gameplay.crafting.ToolShapelessRecipe;
import com.im.infrastructure.platform.Registrar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public final class RecipeRegistry
{
    public static final Registrar<RecipeSerializer<?>> RecipeSerializers = new Registrar<>(BuiltInRegistries.RECIPE_SERIALIZER, Furnacology.ModId);

    public static final Supplier<ToolShapelessRecipe.Serializer> ToolShapeless = RecipeSerializers.register("tool_shapeless", ToolShapelessRecipe.Serializer::new);
}
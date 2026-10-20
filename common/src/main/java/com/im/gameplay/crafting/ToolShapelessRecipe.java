package com.im.gameplay.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

public class ToolShapelessRecipe extends ShapelessRecipe
{
    private static final int MaxMaterials = 8;
    private static final int Wear = 1;

    private final Serializer _serializer;
    private final Ingredient _tool;
    private final List<Ingredient> _materials;
    private final ItemStack _result;

    public ToolShapelessRecipe(Serializer serializer, String group, CraftingBookCategory category, Ingredient tool, List<Ingredient> materials, ItemStack result)
    {
        super(group, category, result, getIngredients(tool, materials));
        _serializer = serializer;
        _tool = tool;
        _materials = List.copyOf(materials);
        _result = result;
    }

    public Ingredient getTool() { return _tool; }
    public List<Ingredient> getMaterials() { return _materials; }
    public ItemStack getResult() { return _result; }
    @Override public RecipeSerializer<?> getSerializer() { return _serializer; }

    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        NonNullList<ItemStack> remaining = super.getRemainingItems(input);

        for (int slot = 0; slot < input.size(); slot++)
        {
            ItemStack stack = input.getItem(slot);

            if (!_tool.test(stack)) continue;

            remaining.set(slot, getWorn(stack));
            break;
        }

        return remaining;
    }

    private static ItemStack getWorn(ItemStack tool)
    {
        ItemStack worn = tool.copyWithCount(1);

        if (!worn.isDamageableItem()) return worn;

        worn.setDamageValue(worn.getDamageValue() + Wear);
        return worn.getDamageValue() >= worn.getMaxDamage() ? ItemStack.EMPTY : worn;
    }

    private static NonNullList<Ingredient> getIngredients(Ingredient tool, List<Ingredient> materials)
    {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        ingredients.add(tool);
        ingredients.addAll(materials);
        return ingredients;
    }

    public static final class Serializer implements RecipeSerializer<ToolShapelessRecipe>
    {
        private final MapCodec<ToolShapelessRecipe> _codec;
        private final StreamCodec<RegistryFriendlyByteBuf, ToolShapelessRecipe> _streamCodec;

        public Serializer()
        {
            _codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ShapelessRecipe::category),
                    Ingredient.CODEC_NONEMPTY.fieldOf("tool").forGetter(ToolShapelessRecipe::getTool),
                    Ingredient.CODEC_NONEMPTY.listOf(1, MaxMaterials).fieldOf("ingredients").forGetter(ToolShapelessRecipe::getMaterials),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ToolShapelessRecipe::getResult)
            ).apply(instance, this::create));

            _streamCodec = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ShapelessRecipe::getGroup,
                    CraftingBookCategory.STREAM_CODEC, ShapelessRecipe::category,
                    Ingredient.CONTENTS_STREAM_CODEC, ToolShapelessRecipe::getTool,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ToolShapelessRecipe::getMaterials,
                    ItemStack.STREAM_CODEC, ToolShapelessRecipe::getResult,
                    this::create);
        }

        public MapCodec<ToolShapelessRecipe> codec() { return _codec; }
        public StreamCodec<RegistryFriendlyByteBuf, ToolShapelessRecipe> streamCodec() { return _streamCodec; }

        private ToolShapelessRecipe create(String group, CraftingBookCategory category, Ingredient tool, List<Ingredient> materials, ItemStack result)
        {
            return new ToolShapelessRecipe(this, group, category, tool, materials, result);
        }
    }
}

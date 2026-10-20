package com.im.databases.registries;

import com.im.Furnacology;
import com.im.composition.visuals.informationdisplay.FoodTooltips;
import com.im.databases.augments.AugmentDatabase;
import com.im.databases.buffs.FoodBuffDatabase;
import com.im.gameplay.buffs.BuffGrant;
import com.im.gameplay.buffs.BuffMix;
import com.im.gameplay.items.*;
import com.im.infrastructure.platform.Registrar;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.im.infrastructure.config.FurnacologyConfig.PerfectCoalItemsSmelted;

public final class ItemRegistry
{
    private static final boolean Cores = false;
    private static final int TicksPerSecond = 20;
    private static final int FurnaceTicksPerItem = 200;
    private static final int CoalScrubberUses = 64;
    private static final int SandwichNutrition = 8, EvilGooNutrition = 2, EvilKebabNutrition = 6;
    private static final int BurgerNutrition = 8, FilledBurgerNutrition = 9, DeluxeBurgerNutrition = 10;
    private static final float SandwichSaturation = 0.8f, BurgerSaturation = 0.8f;

    public static final Registrar<Item> Items = new Registrar<>(BuiltInRegistries.ITEM, Furnacology.ModId);

    public static final Supplier<BlockItem> LivingFurnace = Items.register("living_furnace", () -> new BlockItem(BlockRegistry.LivingFurnace.get(), new Item.Properties()));

    public static final Supplier<TooltipItem> CoalDough = registerFood("coal_dough", food(3, 0.3f, nausea(10)), FoodBuffDatabase.CoalDough);
    public static final Supplier<TooltipItem> HalfCoalDough = registerFood("half_coal_dough", food(2, 0.3f, nausea(5)), FoodBuffDatabase.HalfCoalDough);
    public static final Supplier<TooltipItem> CoalBread = registerFood("coal_bread", food(5, 0.6f, nausea(10)), FoodBuffDatabase.CoalBread);
    public static final Supplier<TooltipItem> CoalBun = registerFood("coal_bun", food(3, 0.6f, nausea(5)), FoodBuffDatabase.CoalBun);
    public static final Supplier<TooltipItem> MagmaCreamSandwich = registerFood("magma_cream_sandwich",
            food(SandwichNutrition, SandwichSaturation, poison(10), nausea(20)), FoodBuffDatabase.MagmaCreamSandwich);
    public static final Supplier<TooltipItem> SlimeSandwich = registerFood("slime_sandwich",
            food(SandwichNutrition, SandwichSaturation, poison(10), nausea(20)), FoodBuffDatabase.SlimeSandwich);
    public static final Supplier<TooltipItem> EvilGoo = registerFood("evil_goo",
            food(EvilGooNutrition, SandwichSaturation * SandwichNutrition / EvilGooNutrition, poison(15), nausea(20)), FoodBuffDatabase.EvilGoo);
    public static final Supplier<TooltipItem> EvilKebab = registerFood("evil_kebab",
            food(EvilKebabNutrition, SandwichSaturation * SandwichNutrition / EvilKebabNutrition, poison(15), nausea(20)), FoodBuffDatabase.EvilKebab);
    public static final Supplier<TooltipItem> Coalburger = registerFood("coalburger", food(BurgerNutrition, BurgerSaturation, poison(5), nausea(10)), FoodBuffDatabase.Coalburger);
    public static final Supplier<TooltipItem> Slimeburger = registerFood("slimeburger",
            food(FilledBurgerNutrition, BurgerSaturation, poison(5), nausea(15)), FoodBuffDatabase.Slimeburger);
    public static final Supplier<TooltipItem> Creamburger = registerFood("creamburger",
            food(FilledBurgerNutrition, BurgerSaturation, poison(5), nausea(15)), FoodBuffDatabase.Creamburger);
    public static final Supplier<TooltipItem> CoalDeluxe = registerFood("coal_deluxe",
            food(DeluxeBurgerNutrition, BurgerSaturation, poison(10), nausea(20)), FoodBuffDatabase.CoalDeluxe);

    public static final Supplier<FuelItem> PerfectCoalChunk = registerItem("perfect_coal_chunk",
            properties -> new FuelItem(properties, () -> (int) Math.round(PerfectCoalItemsSmelted.get() * FurnaceTicksPerItem)), new Item.Properties().rarity(Rarity.EPIC));

    public static final Supplier<ColoredNameItem> LivingEmber = registerItem("living_ember", properties -> new ColoredNameItem(properties, ChatFormatting.RED), new Item.Properties());
    public static final Supplier<ColoredNameItem> GroundLivingEmber = registerItem("ground_living_ember",
            properties -> new ColoredNameItem(properties, ChatFormatting.RED), new Item.Properties());
    public static final Supplier<Item> EmberCore = registerItem("ember_core", Item::new, new Item.Properties(), Cores);
    public static final Supplier<Item> BlazingCore = registerItem("blazing_core", Item::new, new Item.Properties(), Cores);
    public static final Supplier<Item> VoidCore = registerItem("void_core", Item::new, new Item.Properties(), Cores);

    public static final Supplier<TransformItem> FurnaceHeart = registerItem("furnace_heart", properties -> new TransformItem(properties, state -> state.is(Blocks.FURNACE),
            BlockRegistry.LivingFurnace, () -> SoundEvents.WARDEN_HEARTBEAT, ParticleTypes.FLAME, ChatFormatting.RED), new Item.Properties());

    public static final Supplier<AugmentItem> FurnaceDigester = registerAugmentItem("furnace_digester", AugmentDatabase.Digester, ChatFormatting.GREEN);
    public static final Supplier<AugmentItem> SpikyNetherite = registerAugmentItem("spiky_netherite", AugmentDatabase.Nether, ChatFormatting.RED);
    public static final Supplier<AugmentItem> DragonChalice = registerAugmentItem("dragon_chalice", AugmentDatabase.End, ChatFormatting.LIGHT_PURPLE);

    public static final Supplier<CoalPencilItem> CoalPencil = registerItem("coal_pencil", CoalPencilItem::new, new Item.Properties().stacksTo(1));
    public static final Supplier<CoalScrubberItem> CoalScrubber = registerItem("coal_scrubber",
            properties -> new CoalScrubberItem(properties, () -> SoundEvents.GRINDSTONE_USE), new Item.Properties().durability(CoalScrubberUses));

    public static Supplier<AugmentItem> registerAugmentItem(String name, ResourceLocation augment, ChatFormatting color)
    {
        return registerItem(name, properties -> new AugmentItem(properties, augment, () -> SoundEvents.SMITHING_TABLE_USE, color), new Item.Properties());
    }

    public static Supplier<TooltipItem> registerBuffItem(String name, Item.Properties properties, BuffGrant... buffs)
    {
        return registerItem(name, props -> new TooltipItem(props.component(DataComponentRegistry.BuffGrants.get(), List.of(buffs)), FoodTooltips::describe), properties);
    }

    public static Supplier<TooltipItem> registerFood(String name, FoodProperties food, BuffMix buffs)
    {
        return registerItem(name, properties -> new TooltipItem(properties.component(DataComponentRegistry.BuffGrants.get(), buffs.grants())
                .component(DataComponentRegistry.ReplacesFoodBuff.get(), Unit.INSTANCE), FoodTooltips::describe), new Item.Properties().food(food));
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> factory, Item.Properties properties)
    {
        return registerItem(name, factory, properties, true);
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Function<Item.Properties, T> factory, Item.Properties properties, boolean enabled)
    {
        return Items.register(name, () -> factory.apply(properties), enabled);
    }

    private static FoodProperties food(int nutrition, float saturationModifier, MobEffectInstance... effects)
    {
        FoodProperties.Builder food = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationModifier);

        for (MobEffectInstance effect : effects)
        {
            food.effect(effect, 1.0f);
        }

        return food.build();
    }

    private static MobEffectInstance nausea(int seconds) { return new MobEffectInstance(MobEffects.CONFUSION, seconds * TicksPerSecond); }
    private static MobEffectInstance poison(int seconds) { return new MobEffectInstance(MobEffects.POISON, seconds * TicksPerSecond); }
}
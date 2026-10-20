package com.im.composition.visuals.informationdisplay;

import com.im.databases.buffs.BuffDatabase;
import com.im.gameplay.buffs.BuffGrant;
import com.im.infrastructure.config.FurnacologyConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.im.infrastructure.config.FurnacologyConfig.BellowsSpeedBonus;
import static com.im.infrastructure.config.FurnacologyConfig.FoodSpeedPerSaturation;

public final class FoodTooltips
{
    private static final String Key = "tooltip.furnacology.";
    private static final BigDecimal TicksPerSecond = BigDecimal.valueOf(20);
    private static final int SpeedDecimals = 2;
    private static final Component FedToFurnace = Component.translatable(Key + "fed_to_furnace").withStyle(ChatFormatting.GRAY);
    private static final Component WhenEaten = Component.translatable(Key + "when_eaten").withStyle(ChatFormatting.GRAY);

    private static final Map<ResourceLocation, BuffLine> BuffLines = Map.of(
            BuffDatabase.Nourished, getSpeedLine("nourished",  ChatFormatting.AQUA, FoodSpeedPerSaturation),
            BuffDatabase.Bellows, getSpeedLine("bellows",  ChatFormatting.GOLD, BellowsSpeedBonus),
            BuffDatabase.SlimeBellows, getSpeedLine("slime_bellows",  ChatFormatting.GREEN, BellowsSpeedBonus),
            BuffDatabase.StrongBellows, getSpeedLine("strong_bellows",  ChatFormatting.RED, BellowsSpeedBonus),
            BuffDatabase.EmberStrike, new BuffLine("ember_strike",  ChatFormatting.DARK_RED, grant -> new Object[] { getSeconds(grant.duration()) }));

    private static final Map<Holder<MobEffect>, ChatFormatting> EffectColors = Map.of(MobEffects.POISON, ChatFormatting.DARK_GREEN, MobEffects.CONFUSION, ChatFormatting.DARK_PURPLE);

    public static List<Component> describe(ItemStack stack)
    {
        List<Component> lines = new ArrayList<>();
        List<Component> buffs = new ArrayList<>();
        FoodProperties food = stack.get(DataComponents.FOOD);

        for (BuffGrant grant :BuffDatabase.FurnaceDigester.digest(stack))
        {
            BuffLine line =  BuffLines.get(grant.buff());

            if (line != null)
                buffs.add(Component.translatable(Key + "buff." + line.name(), line.args().apply(grant)).withStyle(line.color()));
        }
        if (!buffs.isEmpty())
        {
            lines.add(FedToFurnace);
            lines.addAll(buffs);
        }
        if (food != null && !food.effects().isEmpty())
        {
            lines.add(WhenEaten);
            food.effects().forEach(effect -> lines.add(getEffectLine(effect.effect())));
        }

        return lines;
    }

    private static Component getEffectLine(MobEffectInstance instance)
    {
        Holder<MobEffect> effect = instance.getEffect();
        MutableComponent name = Component.translatable(effect.value().getDescriptionId());
        ChatFormatting color = EffectColors.getOrDefault(effect, effect.value().getCategory().getTooltipFormatting());

        if (instance.getAmplifier() > 0) name = Component.translatable("potion.withAmplifier", name, Component.translatable("potion.potency." + instance.getAmplifier()));

        return Component.translatable(Key + "effect", name, getSeconds(instance.getDuration())).withStyle(color);
    }

    private static BuffLine getSpeedLine(String name, ChatFormatting color, ModConfigSpec.DoubleValue speed)
    {
        return new BuffLine(name, color, grant -> new Object[] { getSpeed(getLoaded(speed) * grant.potency()), getSeconds(grant.duration()) });
    }

    private static double getLoaded(ModConfigSpec.DoubleValue value) { return FurnacologyConfig.Spec.isLoaded() ? value.get() : value.getDefault(); }
    private static String getSeconds(int ticks) { return BigDecimal.valueOf(ticks).divide(TicksPerSecond).stripTrailingZeros().toPlainString(); }

    private static String getSpeed(double value)
    {
        BigDecimal rounded = BigDecimal.valueOf(value).setScale(SpeedDecimals, RoundingMode.HALF_UP).stripTrailingZeros();
        return (rounded.scale() < 1 ? rounded.setScale(1) : rounded).toPlainString();
    }

    private record BuffLine(String name, ChatFormatting color, Function<BuffGrant, Object[]> args) { }

    private FoodTooltips() { }
}

package com.im.ui.hud;

import com.im.Furnacology;
import com.im.ui.GuiSprite;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

public record FurnaceGui(GuiSprite background, GuiSprite cookProgress, GuiSprite fuelRemaining, GuiSprite buffRemaining, GuiSprite buffCharge, GuiSprite speedPanel,
                         GuiSprite instantPanel, GuiSprite foodSlot)
{
    public static final ResourceLocation DefaultBuffIcon = Furnacology.id("buff/default");

    public static FurnaceGui of(String block, @Nullable FurnaceGui fallback)
    {
        return new FurnaceGui(
                getSprite(block, "background", 176, 166, fallback, FurnaceGui::background),
                getSprite(block, "cook_progress", 24, 17, fallback, FurnaceGui::cookProgress),
                getSprite(block, "fuel_remaining", 14, 12, fallback, FurnaceGui::fuelRemaining),
                getSprite(block, "buff_remaining", 13, 14, fallback, FurnaceGui::buffRemaining),
                getSprite(block, "buff_charge", 13, 14, fallback, FurnaceGui::buffCharge),
                getSprite(block, "speed_panel", 50, 166, fallback, FurnaceGui::speedPanel),
                getSprite(block, "instant_buff_panel", 32, 166, fallback, FurnaceGui::instantPanel),
                getSprite(block, "food_slot", 38, 18, fallback, FurnaceGui::foodSlot));
    }

    private static GuiSprite getSprite(String block, String name, int width, int height, @Nullable FurnaceGui fallback, Function<FurnaceGui, GuiSprite> same)
    {
        return new GuiSprite(Furnacology.id("textures/gui/" + block + "/" + name + ".png"), width, height, fallback == null ? null : same.apply(fallback));
    }
}

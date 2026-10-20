package com.im.ui.hud;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.core.lifecycle.typeregistry.ITypeRegistry;
import com.im.core.menu.ComponentMenu;
import com.im.core.menu.MenuDataTracker;
import com.im.core.menu.MenuSlot;
import com.im.core.values.cappedvalues.CappedValueReadOnly;
import com.im.core.values.cappedvalues.ICappedValueReadOnly;
import com.im.gameplay.abilities.AbilityUser;
import com.im.gameplay.digestion.Digestion;
import com.im.gameplay.effects.groups.TemporaryEffectGroup;
import com.im.gameplay.skins.FurnaceSkin;
import com.im.gameplay.skins.SkinSelector;
import com.im.gameplay.smelting.furnace.ExperienceStorage;
import com.im.gameplay.smelting.furnace.Furnace;
import com.im.gameplay.smelting.memory.SmeltingMemoryWrapper;
import com.im.ui.*;
import com.im.ui.UIContent.FillDirection;
import com.im.ui.UIContent.IFillScale;
import com.im.ui.UIContent.Row;
import com.im.ui.UIElement.IContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class LivingFurnaceDocument
{
    private static final int InputX = 56, InputY = 17, InputPriority = 3;
    private static final int FuelX = 56, FuelY = 53, FuelPriority = 1;
    private static final int FoodX = 30, FoodY = 53, FoodPriority = 2;
    private static final int OutputX = 116, OutputY = 35;
    private static final int CookX = 79, CookY = 35;
    private static final int FlameX = 57, FlameY = 37;
    private static final int BuffFlameX = 11, BuffFlameY = 55;
    private static final int FoodSlotX = 11, FoodSlotY = 52;
    private static final int SpeedLabelX = 153, SpeedLabelY = 6;
    private static final GuiArea SpeedToggle = new GuiArea(156, 4, 12, 12);
    private static final int SpeedPanelPadding = 4;
    private static final int InstantPanelPadding = 8;
    private static final int PanelGap = 2;
    private static final int TicksPerSecond = 20;

    public static UIDocument create(ComponentBlockEntity entity, Component title, Supplier<? extends MenuType<ComponentMenu>> menuType, ITypeRegistry<Object> parts)
    {
        Furnace furnace = parts.get(Furnace.class);
        Digestion digestion = parts.get(Digestion.class);
        AbilityUser abilities = parts.get(AbilityUser.class);
        SmeltingMemoryWrapper memory = parts.get(SmeltingMemoryWrapper.class);
        ExperienceStorage experience = parts.get(ExperienceStorage.class);
        SkinSelector<?> skins = parts.get(SkinSelector.class);
        Supplier<FurnaceGui> gui = () -> ((FurnaceSkin) skins.getCurrent()).gui();
        UIDocument document = new UIDocument(entity, title, menuType, () -> gui.get().background(), () -> getSlots(furnace, digestion, experience));
        MenuDataTracker data = document.getData();
        IntSupplier progress = data.track(furnace::getProgress);
        IntSupplier maxProgress = data.track(furnace::getMaxProgress);
        IntSupplier burnTime = data.track(furnace::getBurnTime);
        IntSupplier burnDuration = data.track(furnace::getBurnDuration);
        Supplier<Float> speed = data.trackFloat(furnace::getCurrentSpeed);
        IntSupplier charge = data.track(digestion::getCharge);
        IntSupplier chargeMax = data.track(digestion::getChargeTicks);
        IntSupplier remaining = data.track(() -> getLongestBuff(digestion, TemporaryEffectGroup::getRemaining));
        IntSupplier duration = data.track(() -> getLongestBuff(digestion, TemporaryEffectGroup::getDuration));
        GuiArea buffArea = getArea(BuffFlameX, BuffFlameY, gui.get().buffRemaining());

        UIToggle speedToggle = document.addToggle(SpeedToggle, Component.literal("?"));

        document.add(getArea(FoodSlotX, FoodSlotY, gui.get().foodSlot()), UIContent.image(() -> gui.get().foodSlot()))
                .withVisible(digestion::isDigesting);
        document.add(getArea(FlameX, FlameY, gui.get().fuelRemaining()),
                UIContent.fill(() -> gui.get().fuelRemaining(), FillDirection.BottomToTop, IFillScale.CeilWithBase, () -> getCapped(burnTime, burnDuration)));
        document.add(getArea(CookX, CookY, gui.get().cookProgress()),
                        UIContent.fill(() -> gui.get().cookProgress(), FillDirection.LeftToRight, IFillScale.Linear, () -> getCapped(progress, maxProgress)))
                .withTooltip(() -> Component.translatable("screen.furnacology.smelt_count", memory.getMemory().getCount(furnace.getInputItem())));
        document.add(buffArea, UIContent.fill(() -> gui.get().buffRemaining(), FillDirection.BottomToTop, IFillScale.CeilWithBase, () -> getCapped(remaining, duration)))
                .withVisible(() -> digestion.isDigesting() && remaining.getAsInt() > 0);
        document.add(buffArea, UIContent.fill(() -> gui.get().buffCharge(), FillDirection.BottomToTop, IFillScale.CeilWithBase, () -> getCapped(charge, chargeMax)))
                .withVisible(() -> digestion.isDigesting() && remaining.getAsInt() <= 0);
        document.add(buffArea, IContent.None)
                .withVisible(digestion::isDigesting)
                .withTooltip(() -> getBuffTooltip(remaining.getAsInt(), charge.getAsInt(), chargeMax.getAsInt()));
        document.add(new GuiArea(SpeedLabelX, SpeedLabelY, 0, 0),
                UIContent.label(true, () -> Component.literal(getMultiplier(speed.get())), () -> furnace.getItems().getItem(Furnace.InputSlot)));
        document.add(getPanelArea(gui.get().background().width() + PanelGap, gui.get().speedPanel()),
                        UIContent.panel(() -> gui.get().speedPanel(), SpeedPanelPadding, () -> getSpeedRows(furnace, memory)))
                .withVisible(speedToggle::isOpen);
        document.add(getPanelArea(-gui.get().instantPanel().width() - PanelGap, gui.get().instantPanel()),
                        UIContent.panel(() -> gui.get().instantPanel(), InstantPanelPadding, () -> getInstantRows(abilities)))
                .withVisible(() -> !abilities.getPending().isEmpty());

        return document;
    }

    private static List<MenuSlot> getSlots(Furnace furnace, Digestion digestion, ExperienceStorage experience)
    {
        List<MenuSlot> slots = new ArrayList<>(List.of(
                MenuSlot.of(furnace.getItems(), Furnace.InputSlot, InputX, InputY, InputPriority),
                MenuSlot.of(furnace.getItems(), Furnace.FuelSlot, FuelX, FuelY, FuelPriority),
                MenuSlot.output(furnace.getItems(), Furnace.OutputSlot, OutputX, OutputY, experience::award)));

        if (digestion.isDigesting()) slots.add(MenuSlot.of(digestion.getFood(), 0, FoodX, FoodY, FoodPriority));

        return slots;
    }

    private static List<Row> getSpeedRows(Furnace furnace, SmeltingMemoryWrapper memory)
    {
        return memory.getMemory().getSnapshot().entrySet().stream()
                .sorted(Map.Entry.<Item, Integer>comparingByValue().reversed().thenComparing(entry -> BuiltInRegistries.ITEM.getKey(entry.getKey())))
                .map(entry -> Row.item(new ItemStack(entry.getKey()), Component.literal(getMultiplier(furnace.getBaseSpeed(entry.getKey())))))
                .toList();
    }

    private static List<Row> getInstantRows(AbilityUser abilities)
    {
        return abilities.groupPending().values().stream().map(group -> Row.sprite(group.getFirst().icon().orElse(FurnaceGui.DefaultBuffIcon), group.size())).toList();
    }

    private static Component getBuffTooltip(int remaining, int charge, int chargeMax)
    {
        if (remaining > 0) return Component.translatable("screen.furnacology.buff_active", remaining / TicksPerSecond);

        if (charge > 0 && chargeMax > 0) return Component.translatable("screen.furnacology.buff_charging", Math.max(0, (chargeMax - charge + TicksPerSecond - 1) / TicksPerSecond));

        return Component.translatable("screen.furnacology.food_slot");
    }

    private static int getLongestBuff(Digestion digestion, ToIntFunction<TemporaryEffectGroup> value)
    {
        TemporaryEffectGroup longest = digestion.getLongestActiveBuff();
        return longest == null ? 0 : value.applyAsInt(longest);
    }

    private static ICappedValueReadOnly<Integer> getCapped(IntSupplier value, IntSupplier max) { return new CappedValueReadOnly<>(value.getAsInt(), 0, max.getAsInt()); }
    private static GuiArea getArea(int x, int y, GuiSprite sprite) { return new GuiArea(x, y, sprite.width(), sprite.height()); }
    private static GuiArea getPanelArea(int x, GuiSprite sprite) { return new GuiArea(x, 0, sprite.width(), sprite.height()); }
    private static String getMultiplier(float speed) { return String.format("%.2fx", speed); }

    private LivingFurnaceDocument() { }
}

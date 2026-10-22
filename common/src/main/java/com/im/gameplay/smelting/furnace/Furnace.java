package com.im.gameplay.smelting.furnace;

import com.im.core.events.Event;
import com.im.core.events.EventCounter;
import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.storage.ItemSlots;
import com.im.core.storage.SideAccess;
import com.im.core.values.scaling.IScalingValue;
import com.im.core.values.scaling.ScalingValue;
import com.im.infrastructure.saving.IStateSerializable;
import com.im.infrastructure.saving.ISyncSerializable;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class Furnace implements IStateSerializable, ISyncSerializable
{
    public static final int InputSlot = 0;
    public static final int FuelSlot = 1;
    public static final int OutputSlot = 2;
    private static final String TagInventory = "inventory";
    private static final String TagSmeltTotal = "smelt_total";
    private static final String TagProgress = "progress";
    private static final String TagMaxProgress = "max_progress";
    private static final String TagBurnTime = "burn_time";
    private static final String TagBurnDuration = "burn_duration";
    private static final String TagCooking = "cooking";
    private static final String TagSpeed = "speed";
    private static final String TagInput = "input";
    private static final String TagEjecting = "ejecting";
    private static final double EjectDistance = 0.5 + EntityType.ITEM.getWidth() / 2.0 + 0.02;
    private static final double EjectHeight = 0.25;
    private static final double EjectSpeed = 0.07;
    private static final double EjectLift = 0.05;

    private final ICompositeBlockEntity _entity;
    private final Function<Item, Float> _learnedSpeed;
    private final ToIntFunction<ItemStack> _fuelTime;
    private final BooleanSupplier _ejecting;
    private final ItemSlots _items;
    private final ScalingValue _baseSpeed = new ScalingValue(() -> 0.0f);
    private final ScalingValue _speed = new ScalingValue(() -> getBaseSpeed(getInputItem()));
    private final ScalingValue _doubleChance = new ScalingValue(() -> 0.0f);
    private final EventCounter _smelts = new EventCounter();
    private final Event<Item> _smelted = new Event<>();
    private final Event<ItemStack> _doubled = new Event<>();
    private final RecipeManager.CachedCheck<SingleRecipeInput, SmeltingRecipe> _recipes = RecipeManager.createCheck(RecipeType.SMELTING);
    private Item _lastInput = Items.AIR;
    private Item _syncedInput;
     private Float _syncedSpeed;
    private boolean _syncedEjecting;
    private int _burnTime;
    private int _burnDuration;
    private int _progress;
    private int _maxProgress;
    private boolean _cooking;

    public Furnace(ICompositeBlockEntity entity, Function<Item, Float> learnedSpeed, ToIntFunction<ItemStack> fuelTime, BooleanSupplier ejecting)
    {
        _entity = entity;
        _learnedSpeed = learnedSpeed;
        _fuelTime = fuelTime;
        _ejecting = ejecting;
        _items = new ItemSlots(3, this::isValid, this::onItemsChanged, Furnace::getSideAccess);
    }

    public ItemSlots getItems() { return _items; }
    public IScalingValue getSpeed() { return _speed; }
    public IScalingValue getBaseSpeed() { return _baseSpeed; }
    public IScalingValue getDoubleChance() { return _doubleChance; }
    public Event<Item> getSmelted() { return _smelted; }
    public Event<ItemStack> getDoubled() { return _doubled; }
    public float getBaseSpeed(Item item) { return _baseSpeed.scale(_learnedSpeed.apply(item)); }
    public float getCurrentSpeed() { return _syncedSpeed != null ? _syncedSpeed : _speed.getFinalValue(); }
    public Item getInputItem() { return _syncedInput != null ? _syncedInput : _items.getItem(InputSlot).getItem(); }
    public boolean isServer() { return _entity.isServer(); }
    public boolean isLit() { return _burnTime > 0; }
    public boolean isSmelting() { return isLit() && _cooking; }
    public boolean isEjecting() { return _entity.isServer() ? _ejecting.getAsBoolean() : _syncedEjecting; }
    public boolean hasRecipe() { return findRecipe() != null; }
    public boolean hasPendingSmelt() { return _smelts.isPending(); }
    public void acknowledgeSmelt() { _smelts.acknowledge(); }
    public int getSmeltTotal() { return _smelts.getTotal(); }
    public int getProgress() { return _progress; }
    public void setProgress(int progress) { _progress = progress; }
    public int getMaxProgress() { return _maxProgress; }
    public void setMaxProgress(int maxProgress) { _maxProgress = maxProgress; }
    public int getBurnTime() { return _burnTime; }
    public void setBurnTime(int burnTime) { _burnTime = burnTime; }
    public int getBurnDuration() { return _burnDuration; }

    public void setCooking(boolean cooking)
    {
        if (_cooking == cooking) return;

        _cooking = cooking;
        _entity.requestSync();
    }

    public RecipeHolder<SmeltingRecipe> findRecipe()
    {
        Level level = _entity.getEntityLevel();
        ItemStack input = _items.getItem(InputSlot);

        if (level == null || input.isEmpty()) return null;

        RecipeHolder<SmeltingRecipe> recipe = _recipes.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);

        return recipe != null && (isEjecting() || canOutputAccept(getResultOf(recipe.value()))) ? recipe : null;
    }

    public boolean burnFuel()
    {
        int duration = _fuelTime.applyAsInt(_items.getItem(FuelSlot));

        if (duration <= 0) return false;

        _items.removeItem(FuelSlot, 1);
        _burnTime = duration;
        _burnDuration = duration;
        return true;
    }

    public void smelt(RecipeHolder<SmeltingRecipe> recipe)
    {
        ItemStack result = getResultOf(recipe.value());
        Item smelted = _items.getItem(InputSlot).getItem();

        _items.removeItem(InputSlot, 1);
        deliver(result);
        _smelts.fire();
        _smelted.invoke(smelted);
        _entity.requestSync();
    }

    public void captureState(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.put(TagInventory, _items.save(registries));
        tag.putInt(TagSmeltTotal, _smelts.getTotal());
        tag.putInt(TagProgress, _progress);
        tag.putInt(TagMaxProgress, _maxProgress);
        tag.putInt(TagBurnTime, _burnTime);
        tag.putInt(TagBurnDuration, _burnDuration);
    }

    public void restoreState(CompoundTag tag, HolderLookup.Provider registries)
    {
        _items.load(tag.getCompound(TagInventory), registries);
        _smelts.load(tag.getInt(TagSmeltTotal));
        _progress = tag.getInt(TagProgress);
        _maxProgress = tag.getInt(TagMaxProgress);
        _burnTime = tag.getInt(TagBurnTime);
        _burnDuration = tag.getInt(TagBurnDuration);
    }

    public void captureSync(CompoundTag tag)
    {
        tag.putFloat(TagSpeed, getCurrentSpeed());
        tag.putString(TagInput, BuiltInRegistries.ITEM.getKey(getInputItem()).toString());
        tag.putInt(TagBurnTime, _burnTime);
        tag.putBoolean(TagCooking, _cooking);
        tag.putInt(TagSmeltTotal, _smelts.getTotal());
        tag.putBoolean(TagEjecting, isEjecting());
    }

    public void restoreSync(CompoundTag tag)
    {
        _syncedSpeed = tag.getFloat(TagSpeed);
        _syncedInput = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(tag.getString(TagInput)));
        _burnTime = tag.getInt(TagBurnTime);
        _cooking = tag.getBoolean(TagCooking);
        _smelts.receive(tag.getInt(TagSmeltTotal));
        _syncedEjecting = tag.getBoolean(TagEjecting);
    }

    private void deliver(ItemStack result)
    {
        Level level = _entity.getEntityLevel();

        if (level != null && level.getRandom().nextFloat() < _doubleChance.getFinalValue())
        {
            result.grow(result.getCount());
            _doubled.invoke(result);
        }

        ItemStack left = isEjecting() ? result : _items.insert(OutputSlot, result);

        if (!left.isEmpty()) eject(left);
    }

    private void eject(ItemStack stack)
    {
        Level level = _entity.getEntityLevel();
        if (level == null) return;

        Vec3 out = Vec3.atLowerCornerOf(_entity.getFacing().getNormal());
        Vec3 start = Vec3.atBottomCenterOf(_entity.getEntityPos()).add(out.scale(EjectDistance)).add(0.0, EjectHeight, 0.0);
        ItemEntity item = new ItemEntity(level, start.x, start.y, start.z, stack, out.x * EjectSpeed, EjectLift, out.z * EjectSpeed);

        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }

    private ItemStack getResultOf(SmeltingRecipe recipe) { return recipe.assemble(new SingleRecipeInput(_items.getItem(InputSlot)), _entity.getEntityLevel().registryAccess()); }

    private boolean canOutputAccept(ItemStack result)
    {
        ItemStack output = _items.getItem(OutputSlot);
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private boolean canSmelt(ItemStack stack)
    {
        Level level = _entity.getEntityLevel();
        return level != null && !stack.isEmpty() && _recipes.getRecipeFor(new SingleRecipeInput(stack), level).isPresent();
    }

    private boolean isValid(int slot, ItemStack stack)
    {
        return switch (slot)
        {
            case FuelSlot -> _fuelTime.applyAsInt(stack) > 0;
            case InputSlot -> canSmelt(stack);
            default -> true;
        };
    }

    private void onItemsChanged()
    {
        Item input = _items.getItem(InputSlot).getItem();
        _entity.markEntityChanged();
        if (input == _lastInput) return;
        _lastInput = input;
        _entity.requestSync();
    }

    private static SideAccess getSideAccess(Direction side)
    {
        return switch (side)
        {
            case UP -> SideAccess.insert(InputSlot);
            case DOWN -> SideAccess.extract(OutputSlot);
            default -> SideAccess.insert(FuelSlot);
        };
    }
}
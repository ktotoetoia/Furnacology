package com.im.core.storage;

import com.im.core.lifecycle.blockentities.IHaveDrops;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ItemSlots implements WorldlyContainer, IHaveDrops
{
    private static final int Stack = 64;

    private final NonNullList<ItemStack> _stacks;
    private final BiPredicate<Integer, ItemStack> _valid;
    private final Runnable _changed;
    private final Function<Direction, SideAccess> _sides;

    public ItemSlots(int size, BiPredicate<Integer, ItemStack> valid, Runnable changed) { this(size, valid, changed, side -> SideAccess.None); }

    public ItemSlots(int size, BiPredicate<Integer, ItemStack> valid, Runnable changed, Function<Direction, SideAccess> sides)
    {
        _stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        _valid = valid;
        _changed = changed;
        _sides = sides;
    }

    public ItemStack insert(int slot, ItemStack stack)
    {
        if (stack.isEmpty() || !canPlaceItem(slot, stack)) return stack;

        ItemStack existing = _stacks.get(slot);
        int room = getMaxStackSize(stack) - existing.getCount();

        if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(stack, existing) || room <= 0) return stack;

        int moved = Math.min(room, stack.getCount());

        if (existing.isEmpty()) _stacks.set(slot, stack.copyWithCount(moved));
        else existing.grow(moved);

        setChanged();
        return stack.copyWithCount(stack.getCount() - moved);
    }

    public CompoundTag save(HolderLookup.Provider registries) { return ContainerHelper.saveAllItems(new CompoundTag(), _stacks, registries); }

    public void load(CompoundTag tag, HolderLookup.Provider registries)
    {
        _stacks.clear();
        ContainerHelper.loadAllItems(tag, _stacks, registries);
    }

    public List<ItemStack> getDrops() { return List.copyOf(_stacks); }

    public int[] getSlotsForFace(Direction side)
    {
        SideAccess access = _sides.apply(side);
        return IntStream.of(access.insert(), access.extract()).filter(slot -> slot != SideAccess.NoSlot).distinct().toArray();
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) { return side != null && _sides.apply(side).insert() == slot && canPlaceItem(slot, stack); }
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return _sides.apply(side).extract() == slot; }
    public int getContainerSize() { return _stacks.size(); }
    public boolean isEmpty() { return _stacks.stream().allMatch(ItemStack::isEmpty); }
    public ItemStack getItem(int slot) { return _stacks.get(slot); }
    public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(_stacks, slot); }
    @Override public int getMaxStackSize() { return Stack; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return _valid.test(slot, stack); }
    public boolean stillValid(Player player) { return true; }
    public void setChanged() { _changed.run(); }

    public ItemStack removeItem(int slot, int amount)
    {
        ItemStack removed = ContainerHelper.removeItem(_stacks, slot, amount);

        if (!removed.isEmpty()) setChanged();

        return removed;
    }

    public void setItem(int slot, ItemStack stack)
    {
        _stacks.set(slot, stack);
        stack.limitSize(getMaxStackSize(stack));
        setChanged();
    }

    public void clearContent()
    {
        _stacks.clear();
        setChanged();
    }
}
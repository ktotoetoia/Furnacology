package com.im.core.menu;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.ui.UIDocument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ComponentMenu extends AbstractContainerMenu
{
    public static final int InventoryX = 8;
    public static final int InventoryY = 84;
    private static final int SlotSize = 18;
    private static final int InventoryColumns = 9;
    private static final int InventoryRows = 3;
    private static final int HotbarGap = 4;
    private final ComponentBlockEntity _componentBlockEntity;
    private final ContainerLevelAccess _access;
    private final Block _block;
    private final int _machineSlots;
    private final List<Integer> _shiftTargets;

    public ComponentMenu(MenuType<?> type, int containerId, Inventory inventory, BlockPos pos)
    {
        this(type, containerId, inventory, (ComponentBlockEntity) inventory.player.level().getBlockEntity(pos));
    }

    public ComponentMenu(MenuType<?> type, int containerId, Inventory inventory, ComponentBlockEntity entity)
    {
        super(type, containerId);
        _componentBlockEntity = entity;
        _access = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        _block = entity.getBlockState().getBlock();

        UIDocument document = entity.get(UIDocument.class);
        List<MenuSlot> machine = document.getSlots();

        machine.forEach(slot -> addSlot(createSlot(slot)));
        _machineSlots = machine.size();
        _shiftTargets = IntStream.range(0, machine.size()).filter(index -> !machine.get(index).takeOnly()).boxed()
                .sorted(Comparator.comparingInt((Integer index) -> machine.get(index).priority()).reversed()).toList();

        addPlayerInventory(inventory);
        document.getData().addDataSlots(this::addDataSlot);
    }

    public ComponentBlockEntity getEntity() { return _componentBlockEntity; }

    @Override
    public boolean stillValid(Player player) { return AbstractContainerMenu.stillValid(_access, player, _block); }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = slots.get(index);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (!(index < _machineSlots ? moveToPlayer(stack) : moveToMachine(stack))) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        slot.onTake(player, stack);
        return original;
    }

    private static Slot createSlot(MenuSlot slot)
    {
        return new Slot(slot.items(), slot.index(), slot.x(), slot.y())
        {
            @Override
            public boolean mayPlace(ItemStack stack) { return !slot.takeOnly() && container.canPlaceItem(getContainerSlot(), stack); }
            @Override
            public void onTake(Player player, ItemStack stack)
            {
                slot.onTake().accept(player);
                super.onTake(player, stack);
            }
        };
    }

    private void addPlayerInventory(Inventory inventory)
    {
        for (int row = 0; row < InventoryRows; row++)
        {
            for (int column = 0; column < InventoryColumns; column++)
            {
                addSlot(new Slot(inventory, column + (row + 1) * InventoryColumns, InventoryX + column * SlotSize, InventoryY + row * SlotSize));
            }
        }

        for (int column = 0; column < InventoryColumns; column++)
        {
            addSlot(new Slot(inventory, column, InventoryX + column * SlotSize, InventoryY + InventoryRows * SlotSize + HotbarGap));
        }
    }

    private boolean moveToPlayer(ItemStack stack) { return moveItemStackTo(stack, _machineSlots, _machineSlots + InventoryColumns * (InventoryRows + 1), true); }

    private boolean moveToMachine(ItemStack stack)
    {
        boolean moved = false;

        for (int target : _shiftTargets)
        {
            if (stack.isEmpty()) break;
            moved |= moveItemStackTo(stack, target, target + 1, false);
        }

        return moved;
    }
}
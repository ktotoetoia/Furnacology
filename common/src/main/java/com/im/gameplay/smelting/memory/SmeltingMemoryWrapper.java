package com.im.gameplay.smelting.memory;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.infrastructure.saving.IItemDataSerializable;
import com.im.infrastructure.saving.IStateSerializable;
import com.im.infrastructure.saving.ISyncSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class SmeltingMemoryWrapper implements IStateSerializable, ISyncSerializable, IItemDataSerializable
{
    private static final String TagMemory = "memory";
    private static final String TagCurve = "speed_curve";

    private final SmeltingMemory _memory = new SmeltingMemory();
    private final ICompositeBlockEntity _entity;
    private final Supplier<SpeedCurve> _curve;
    private final Supplier<DataComponentType<SmeltingMemoryData>> _itemData;
    private SpeedCurve _syncedCurve;

    public SmeltingMemoryWrapper(ICompositeBlockEntity entity, Supplier<SpeedCurve> curve, Supplier<DataComponentType<SmeltingMemoryData>> itemData)
    {
        _entity = entity;
        _curve = curve;
        _itemData = itemData;
    }

    public SmeltingMemory getMemory()
    {
        return _memory;
    }
    public float getSpeedFor(Item item)
    {
        return getCurve().getSpeedFor(_memory.getCount(item));
    }

    public void record(Item item)
    {
        _memory.recordSmelt(item);
        _entity.requestSync();
    }

    public void captureState(CompoundTag tag, HolderLookup.Provider registries) { tag.put(TagMemory, _memory.save()); }
    public void restoreState(CompoundTag tag, HolderLookup.Provider registries) { _memory.load(tag.getCompound(TagMemory)); }

    public void captureSync(CompoundTag tag)
    {
        tag.put(TagMemory, _memory.save());
        tag.put(TagCurve, SpeedCurve.DataCodec.encodeStart(NbtOps.INSTANCE, getCurve()).getOrThrow());
    }

    public void restoreSync(CompoundTag tag)
    {
        _memory.load(tag.getCompound(TagMemory));
        SpeedCurve.DataCodec.parse(NbtOps.INSTANCE, tag.getList(TagCurve, Tag.TAG_COMPOUND)).result().ifPresent(curve -> _syncedCurve = curve);
    }

    public void collectImplicit(DataComponentMap.Builder components)
    {
        if (!_memory.isEmpty() ) components.set(_itemData.get(), _memory.toData());
    }

    public void applyImplicit(IDataSource source)
    {
        SmeltingMemoryData data = source.get(_itemData.get());
        if (data !=  null) _memory.load(data);
    }

    @Override
    public void removeFromTag(CompoundTag tag) { tag.remove(TagMemory); }

    private SpeedCurve getCurve() { return _syncedCurve != null ? _syncedCurve : _curve.get(); }
}

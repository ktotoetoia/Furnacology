package com.im.gameplay.augments;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.lifecycle.blockentities.IHaveDrops;
import com.im.gameplay.effects.containers.IEffectContainer;
import com.im.gameplay.effects.effectfactories.EffectContext;
import com.im.gameplay.effects.groups.IEffectGroup;
import com.im.infrastructure.saving.IPersistentSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AugmentContainer implements IPersistentSerializable, IHaveDrops
{
    private static final String TagUpgrades = "upgrades";

    private final ICompositeBlockEntity _entity;
    private final IEffectContainer _effects;
    private final Function<ResourceLocation, Augment> _augments;
    private final Map<ResourceLocation, Integer> _installed = new LinkedHashMap<>();
    private final List<IEffectGroup> _groups = new ArrayList<>();

    public AugmentContainer(ICompositeBlockEntity entity, IEffectContainer effects, Function<ResourceLocation, Augment> augments)
    {
        _entity = entity;
        _effects = effects;
        _augments = augments;
    }

    public boolean isEmpty() { return _installed.isEmpty(); }
    public int getTotal() { return _installed.values().stream().mapToInt(Integer::intValue).sum(); }
    public int getCount(ResourceLocation id) { return _installed.getOrDefault(id, 0); }
    public boolean canApply(ResourceLocation id) { return getCount(id) < getMaxStacks(id); }

    public int getMaxStacks(ResourceLocation id)
    {
        Augment augment = _augments.apply(id);
        return augment == null ? 0 : Math.max(0, augment.maxStacks().applyAsInt(_entity));
    }

    public boolean apply(ResourceLocation id)
    {
        if (!canApply(id) ) return false;

        Map<ResourceLocation, Integer> counts =  new LinkedHashMap<>(_installed);
        counts.merge(id, 1, Integer::sum);
        install(counts);
        markChanged();
        return true;
    }

    public List<ItemStack> removeAll()
    {
        List<ItemStack> removed = getDrops();

        install(Map.of());
        markChanged();
        return removed;
    }

    public List<ItemStack> getDrops()
    {
        List<ItemStack> drops = new ArrayList<>();

        _installed.forEach((id, stacks) ->
        {
            Augment augment = _augments.apply(id);

            if (augment != null) drops.add(augment.toStack(stacks));
        });

        return drops;
    }

    public void capture(CompoundTag tag) { tag.put(TagUpgrades, AugmentData.DataCodec.encodeStart(NbtOps.INSTANCE, new AugmentData(_installed)).getOrThrow()); }
    public void restore(CompoundTag tag) { install(AugmentData.DataCodec.parse(NbtOps.INSTANCE, tag.getCompound(TagUpgrades)).result().map(AugmentData::counts).orElse(Map.of())); }

    private void markChanged()
    {
        _entity.markEntityChanged();
        _entity.requestSync();
    }

    private void install(Map<ResourceLocation, Integer> counts)
    {
        if (counts.equals(_installed)) return;

        _groups.forEach(_effects::removeGroup);
        _groups.clear();
        _installed.clear();

        counts.forEach((id, stacks) ->
        {
            Augment augment = stacks > 0 ? _augments.apply(id) : null;

            if (stacks > 0) _installed.put(id, stacks);
            if (augment != null) _groups.add(augment.create(new EffectContext(_entity, stacks)));
        });

        _groups.forEach(_effects::addGroup);
    }
}
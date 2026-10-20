package com.im.gameplay.digestion;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.lifecycle.blockentities.IHaveDrops;
import com.im.core.statemachine.statemachine.ITickable;
import com.im.core.storage.ItemSlots;
import com.im.gameplay.abilities.AbilityUser;
import com.im.gameplay.abilities.PendingAbility;
import com.im.gameplay.buffs.ActiveBuff;
import com.im.gameplay.buffs.Buff;
import com.im.gameplay.buffs.BuffEffectGroup;
import com.im.gameplay.buffs.BuffGrant;
import com.im.gameplay.digestion.digesters.IDigester;
import com.im.gameplay.effects.containers.IEffectContainer;
import com.im.gameplay.effects.effectfactories.EffectContext;
import com.im.infrastructure.saving.IStateSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public class Digestion implements ITickable, IStateSerializable, IHaveDrops
{
    private static final String TagFood = "food";
    private static final String TagBuffCharge = "buff_charge";
    private static final String TagActiveBuffs = "active_buffs";

    private final ICompositeBlockEntity _entity;
    private final IEffectContainer _effects;
    private final AbilityUser _abilities;
    private final Function<ResourceLocation, Buff> _buffs;
    private final IDigester<ItemStack> _digester;
    private final Supplier<Integer> _chargeTicks;
    private final BooleanSupplier _digesting;
    private final ItemSlots _food;
    private int _charge;

    public Digestion(ICompositeBlockEntity entity, IEffectContainer effects, AbilityUser abilities, Function<ResourceLocation, Buff> buffs, IDigester<ItemStack> digester,
                     Supplier<Integer> chargeTicks, BooleanSupplier digesting)
    {
        _entity = entity;
        _effects = effects;
        _abilities = abilities;
        _buffs = buffs;
        _digester = digester;
        _chargeTicks = chargeTicks;
        _digesting = digesting;
        _food = new ItemSlots(1, (slot, stack) -> isDigesting() && !digest(stack).isEmpty(), entity::setChanged);
    }

    public ItemSlots getFood() { return _food; }
    public List<ItemStack> getDrops() { return _food.getDrops(); }
    public boolean isDigesting() { return _digesting.getAsBoolean(); }
    public int getCharge() { return _charge; }
    public int getChargeTicks() { return _chargeTicks.get(); }
    public List<BuffGrant> digest(ItemStack stack) { return stack.isEmpty() ? List.of() : _digester.digest(stack); }
    public List<BuffEffectGroup> getActiveBuffs() { return _effects.getGroups(BuffEffectGroup.class); }
    @Nullable public BuffEffectGroup getLongestActiveBuff() { return getActiveBuffs().stream().max(Comparator.comparingInt(BuffEffectGroup::getRemaining)).orElse(null); }

    public void tick()
    {
        if (!_entity.isServer() || isDigesting() || _food.getItem(0).isEmpty()) return;

        ItemStack stack = _food.getItem(0);
        _food.setItem(0, ItemStack.EMPTY);
        _entity.dropItem(stack);
    }

    public void updateDigestion(boolean smelting)
    {
        boolean hungry = isDigesting() && getActiveBuffs().isEmpty() && smelting;
        List<BuffGrant> grants = hungry ? digest(_food.getItem(0)).stream().filter(grant -> _buffs.apply(grant.buff()) != null).toList() : List.of();

        if (grants.isEmpty())
        {
            _charge = 0;
            return;
        }

        if (++_charge < getChargeTicks()) return;

        _charge = 0;
        _food.removeItem(0, 1);
        grants.forEach(this::grant);
    }

    public void captureState(CompoundTag tag, HolderLookup.Provider registries)
    {
        tag.put(TagFood, _food.save(registries));
        tag.putInt(TagBuffCharge, _charge);
        tag.put(TagActiveBuffs, ActiveBuff.ListCodec.encodeStart(NbtOps.INSTANCE, getActiveBuffs().stream().map(BuffEffectGroup::toActiveBuff).toList()).getOrThrow());
    }

    public void restoreState(CompoundTag tag, HolderLookup.Provider registries)
    {
        _food.load(tag.getCompound(TagFood), registries);
        _charge = tag.getInt(TagBuffCharge);
        getActiveBuffs().forEach(_effects::removeGroup);
        ActiveBuff.ListCodec.parse(NbtOps.INSTANCE, tag.getList(TagActiveBuffs, Tag.TAG_COMPOUND)).resultOrPartial().ifPresent(buffs -> buffs.forEach(this::restore));
    }

    private void grant(BuffGrant grant)
    {
        Buff buff = _buffs.apply(grant.buff());

        if (!buff.effects().isEmpty()) _effects.addGroup(buff.create(grant.buff(), new EffectContext(_entity, grant.potency()), grant.duration(), grant.duration()));

        if (buff.instant() != null) _abilities.add(new PendingAbility(grant.buff(), grant.icon().or(() -> Optional.ofNullable(buff.release().icon()))));

        _entity.requestSync();
    }

    private void restore(ActiveBuff active)
    {
        Buff buff = _buffs.apply(active.buff());

        if (buff != null && !buff.effects().isEmpty()) _effects.addGroup(buff.create(active.buff(), new EffectContext(_entity, active.potency()), active.duration(), active.remaining()));
    }
}

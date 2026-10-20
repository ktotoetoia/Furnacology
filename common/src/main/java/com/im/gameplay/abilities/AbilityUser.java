package com.im.gameplay.abilities;

import com.im.core.events.EventCounter;
import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.abilities.blows.Blow;
import com.im.gameplay.buffs.Buff;
import com.im.infrastructure.saving.IStateSerializable;
import com.im.infrastructure.saving.ISyncSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AbilityUser implements IStateSerializable, ISyncSerializable
{
    private static final String TagPending = "pending_buffs";
    private static final String TagReleaseTotal = "release_total";
    private static final String TagReleaseLeader = "release_leader";
    private static final String TagReleaseDuration = "release_duration";
    private final ICompositeBlockEntity _entity;
    private final Function<ResourceLocation, Buff> _buffs;
    private final Supplier<Integer> _releaseDelayTicks;
    private final List<PendingAbility> _pending = new ArrayList<>();
    private final EventCounter _releases = new EventCounter();
    @Nullable private ResourceLocation _releaseLeader;
    private int _releaseDuration = 1;
    private int _releaseTicks;
    private int _quietTicks;
    public AbilityUser(ICompositeBlockEntity entity, Function<ResourceLocation, Buff> buffs, Supplier<Integer> releaseDelayTicks)
    {
        _entity = entity;
        _buffs = buffs;
        _releaseDelayTicks = releaseDelayTicks;
    }

    public List<PendingAbility> getPending() { return Collections.unmodifiableList(_pending); }
    public void add(PendingAbility ability) { _pending.add(ability); }
    public void updateQuietTicks(boolean working) { _quietTicks = working ? 0 : _quietTicks + 1; }
    public boolean isReleaseDone() { return _releaseTicks >= _releaseDuration; }
    public void acknowledgeRelease() { _releases.acknowledge(); }
    public void resetReleaseTicks() { _releaseTicks = 0; }
    public int nextReleaseTick() { return _releaseTicks++; }

    public boolean isReleaseDue()
    {
        if (!_entity.isServer()) return _releases.isPending();

        return !_pending.isEmpty() && _quietTicks >= _releaseDelayTicks.get();
    }

    public Map<ResourceLocation, List<PendingAbility>> groupPending()
    {
        return _pending.stream().collect(Collectors.groupingBy(PendingAbility::buff, LinkedHashMap::new, Collectors.toList()));
    }

    public List<AbilityRelease> getReleases()
    {
        List<AbilityRelease> releases = new ArrayList<>();

        groupPending().forEach((id, group) ->
        {
            Buff buff = _buffs.apply(id);
            if (buff != null && buff.instant() != null) releases.add(new AbilityRelease(id, buff, group.size(), Math.max(1, buff.release().duration().get())));
        });

        return releases;
    }

    public List<Blow> getReleaseBlows()
    {
        Buff leader = _releaseLeader == null ? null : _buffs.apply(_releaseLeader);
        return leader == null||leader.release() == null ? List.of() : leader.release().blows();
    }

    public void beginRelease()
    {
        AbilityRelease leader = null;
        int longest = 1;

        for (AbilityRelease release : getReleases())
        {
            longest = Math.max(longest, release.duration());
            if (leader == null || release.degree() > leader.degree()) leader = release;
        }

        _releaseLeader = leader == null ? null : leader.id();
        _releaseDuration = longest;
        _releases.fire();
        _entity.requestSync();
    }

    public List<AbilityRelease> release()
    {
        List<AbilityRelease> releases = getReleases();

        _pending.clear();
        _entity.requestSync();
        return releases;
    }

    public void captureState(CompoundTag tag, HolderLookup.Provider registries) { tag.put(TagPending, PendingAbility.ListCodec.encodeStart(NbtOps.INSTANCE, _pending).getOrThrow()); }
    public void restoreState(CompoundTag tag, HolderLookup.Provider registries) { restorePending(tag); }

    public void captureSync(CompoundTag tag)
    {
        tag.putInt(TagReleaseTotal, _releases.getTotal());
        tag.putInt(TagReleaseDuration, _releaseDuration);
        tag.put(TagPending, PendingAbility.ListCodec.encodeStart(NbtOps.INSTANCE, _pending).getOrThrow());

        if (_releaseLeader != null) tag.putString(TagReleaseLeader, _releaseLeader.toString());
    }

    public void restoreSync(CompoundTag tag)
    {
        _releaseLeader = tag.contains(TagReleaseLeader) ? ResourceLocation.tryParse(tag.getString(TagReleaseLeader)) : null;
        _releaseDuration = Math.max(1, tag.getInt(TagReleaseDuration));
        restorePending(tag);
        _releases.receive(tag.getInt(TagReleaseTotal));
    }

    private void restorePending(CompoundTag tag)
    {
        _pending.clear();
        PendingAbility.ListCodec.parse(NbtOps.INSTANCE, tag.getList(TagPending, Tag.TAG_COMPOUND)).resultOrPartial().ifPresent(_pending::addAll);
    }
}
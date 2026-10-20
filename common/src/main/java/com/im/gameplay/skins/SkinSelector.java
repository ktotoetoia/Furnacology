package com.im.gameplay.skins;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.infrastructure.saving.IPersistentSerializable;
import net.minecraft.nbt.CompoundTag;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SkinSelector<T extends ISkin> implements IPersistentSerializable
{
    public static final int Automatic = -1;
    private static final String TagSkin = "skin";
    private static final int DefaultSkin = 0;

    private final ICompositeBlockEntity _entity;
    private final List<T> _skins;
    private final Supplier<? extends Collection<? extends ISkinRequest>> _requests;
    private int _chosen = Automatic;

    public SkinSelector(ICompositeBlockEntity entity,List<T> skins, Supplier<? extends Collection<? extends ISkinRequest>> requests)
    {
        if (skins.isEmpty()) throw new IllegalArgumentException("A skin selector needs at least one skin");

        _entity = entity;
        _skins = List.copyOf(skins);
        _requests = requests;
    }

    public T getCurrent() { return _skins.get(getCurrentIndex()); }
    public boolean isAutomatic() { return _chosen == Automatic || !getAvailable().contains(_chosen); }

    public int getCurrentIndex()
    {
        return !isAutomatic() ? _chosen : getRequests().max(Comparator.comparingInt(Request::priority).thenComparingInt(Request::skin)).map(Request::skin).orElse(DefaultSkin);

    }

    public List<Integer> getAvailable()
    {
        Set<Integer> available = new TreeSet<>(Set.of(DefaultSkin));

        getRequests().forEach(request -> available.add(request.skin()));
        return List.copyOf(available);
    }

    public T cycle()
    {
        List<Integer> available = getAvailable();
        int next = isAutomatic() ? 0 : available.indexOf(_chosen) + 1;

        _chosen = next < available.size() ? available.get(next) : Automatic;
        _entity.setChanged();
        _entity.requestSync();
        return getCurrent();
    }

    public void capture(CompoundTag tag) { tag.putInt(TagSkin, _chosen); }

    public void restore(CompoundTag tag)
    {
        int index = tag.contains(TagSkin) ? tag.getInt(TagSkin) : Automatic;
        _chosen = index >= 0 && index < _skins.size() ? index : Automatic;
    }

    private Stream<Request> getRequests()
    {
        return _requests.get().stream().map(request -> new Request(indexOf(request), request.priority())).filter(request -> request.skin() >= 0);
    }

    private int indexOf(ISkinRequest request)
    {
        return IntStream.range(0, _skins.size()).filter(index -> _skins.get(index).id().equals(request.skin())).findFirst().orElse(-1);
    }

    private record Request(int skin, int priority) { }
}
package com.im.gameplay.buffs;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

public final class BuffMixer
{
    private final List<Fusion> _fusions;

    public BuffMixer() { this(List.of()); }
    private BuffMixer(List<Fusion> fusions) { _fusions = List.copyOf(fusions); }

    public BuffMixer fuse(ResourceLocation first, ResourceLocation second, ResourceLocation into)
    {
        List<Fusion> extended = new ArrayList<>(_fusions);
        extended.add(new Fusion(first, second, into));
        return new BuffMixer(extended);
    }

    public BuffMix mix(BuffMix... ingredients)
    {
        Map<ResourceLocation, Total> totals = new LinkedHashMap<>();

        for (BuffMix ingredient : ingredients)
        {
            for (BuffGrant grant : ingredient.grants())
            {
                totals.computeIfAbsent(grant.buff(), buff -> new Total()).add(grant.duration(), grant.potency(), grant.icon());
            }
        }

        _fusions.forEach(fusion -> fusion.apply(totals));

        return new BuffMix(totals.entrySet().stream().map(entry -> entry.getValue().toGrant(entry.getKey())).toList());
    }

    private record Fusion(ResourceLocation first, ResourceLocation second, ResourceLocation into)
    {
        private void apply(Map<ResourceLocation, Total> totals)
        {
            if (!totals.containsKey(into) && !(totals.containsKey(first) && totals.containsKey(second))) return;

            Total fused = totals.computeIfAbsent(into, buff -> new Total());
            fused.absorb(totals.remove(first));
            fused.absorb(totals.remove(second));
        }
    }

    private static final class Total
    {
        private int _duration;
        private float _weightedPotency;
        private Optional<ResourceLocation> _icon = Optional.empty();

        private float getPotency() { return _duration > 0 ? _weightedPotency / _duration : 1.0f; }
        private BuffGrant toGrant(ResourceLocation buff) { return new BuffGrant(buff, _duration, getPotency(), _icon); }

        private void add(int ticks, float potency, Optional<ResourceLocation> icon)
        {
            _duration += ticks;
            _weightedPotency += potency * ticks;

            if (_icon.isEmpty()) _icon = icon;
        }

        private void absorb(@Nullable Total other)
        {
            if (other != null) add(other._duration, other.getPotency(), other._icon);
        }
    }
}
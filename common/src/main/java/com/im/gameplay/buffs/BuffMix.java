package com.im.gameplay.buffs;

import java.util.List;

public record BuffMix(List<BuffGrant> grants)
{
    public static BuffMix of(BuffGrant... grants) { return new BuffMix(List.of(grants)); }

    public BuffMix divide(int parts)
    {
        return new BuffMix(grants.stream().map(grant -> new BuffGrant(grant.buff(), grant.duration() / parts, grant.potency(), grant.icon())).toList());
    }
}
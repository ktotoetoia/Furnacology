package com.im.composition.gameplay.contracts;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface IHitContract
{
    void onHit(ICompositeBlockEntity source, List<LivingEntity> hits);
    default IHitContract then(IHitContract next)
    {
        return (source, hits) ->
        {
            onHit(source, hits);
            next.onHit(source, hits);
        };
    }
}
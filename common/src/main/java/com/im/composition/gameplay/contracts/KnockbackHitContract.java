package com.im.composition.gameplay.contracts;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class KnockbackHitContract implements IHitContract
{
    private final Supplier<Float> _strength;
    public KnockbackHitContract(Supplier<Float> strength) { _strength = strength; }
    public void onHit(ICompositeBlockEntity source, List<LivingEntity> hits)
    {
        float push = _strength.get();
        Vec3 origin = Vec3.atCenterOf(source.getEntityPos());

        if (push <= 0.0f) return;

        for (LivingEntity hit : hits)
        {
            hit.knockback(push, origin.x - hit.getX(), origin.z - hit.getZ());
            hit.hurtMarked = true;
        }
    }
}
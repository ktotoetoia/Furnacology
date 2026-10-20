package com.im.composition.gameplay.contracts;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.area.IArea;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

//TODO add raycast system
//only deal damage when can be hit in a straight line

public class AreaDamageSmeltContract implements ISmeltContract
{
    private final Predicate<LivingEntity> _targets;
    private final Supplier<Float> _damage;
    private final Supplier<Float> _burnSeconds;
    private final IHitContract _hitContract;

    public AreaDamageSmeltContract(Predicate<LivingEntity> targets, Supplier<Float> damage, Supplier<Float> burnSeconds, IHitContract onHit)
    {
        _targets = targets;
        _damage = damage;
        _burnSeconds = burnSeconds;
        _hitContract = onHit;
    }

    public void onSmelted(ICompositeBlockEntity furnace)
    {
        float amount = _damage.get();
        Level level = furnace.getLevel();
        IArea area = furnace.get(IArea.class);

        if (amount <= 0.0f || level == null || area == null) return;

        DamageSource source = level.damageSources().magic();
        float burn = _burnSeconds.get();
        List<LivingEntity> hits = new ArrayList<>();

        for (LivingEntity entity : area.getEntities(LivingEntity.class, _targets))
        {
            entity.invulnerableTime = 0;

            if (!entity.hurt(source, amount)) continue;

            if (burn > 0.0f) entity.igniteForSeconds(burn);

            hits.add(entity);
        }

        if (!hits.isEmpty()) _hitContract.onHit(furnace, hits);
    }
}

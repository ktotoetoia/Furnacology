package com.im.composition.gameplay.abilities;

import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.gameplay.abilities.IAbility;
import com.im.gameplay.abilities.blows.BlowPicker;
import com.im.gameplay.area.AirStream;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class BlastAbility implements IAbility
{
    private static final double StreamHeight = 2.0;
    private final Supplier<Float> _force;
    private final Supplier<Float> _damage;
    private final Supplier<Float> _burnSeconds;
    private final Supplier<Integer> _width;
    private final Supplier<Integer> _range;

    public BlastAbility(Supplier<Float> force, Supplier<Float> damage, Supplier<Float> burnSeconds, Supplier<Integer> range, Supplier<Integer> width)
    {
        _force = force;
        _damage = damage;
        _burnSeconds = burnSeconds;
        _range = range;
        _width = width;
    }

    @Override public void tick(ICompositeBlockEntity caster, int degree, float normalizedTime)
    {
        Level level = caster.getEntityLevel();
        Direction facing = caster.getFacing();
        float speed = _force.get() * degree;
        float hit = _damage.get() * degree;
        Vec3 away = new Vec3(facing.getStepX(), 0.0, facing.getStepZ());
        DamageSource fire = level.damageSources().inFire();
        AirStream stream = AirStream.trace(level, caster.getEntityPos(), facing, _range.get(), _width.get(), StreamHeight);

        if (!stream.isEmpty())
        {
            for (Entity entity : level.getEntities((Entity) null, stream.getBounds(), EntitySelector.NO_SPECTATORS))
            {
                if (!stream.touches(entity.getBoundingBox())) continue;
                if (hit > 0.0f && entity instanceof LivingEntity && entity.hurt(fire, hit)) entity.igniteForSeconds(_burnSeconds.get());

                Vec3 motion = entity.getDeltaMovement();
                double current = motion.dot(away);

                if (current >= speed) continue;

                entity.setDeltaMovement(motion.add(away.scale(speed - current)));
                entity.hurtMarked = true;
            }
        }

        if (speed > 0.0f)
            caster.tryGet(BlowPicker.class).ifPresent(blow -> blow.spray(stream::getReach));
    }
}
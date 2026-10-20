package com.im.visuals.particles.client;

import com.im.visuals.particles.TimedParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TimedFlameProvider implements ParticleProvider<TimedParticleOptions>
{
    private final SpriteSet _sprites;
    private final float _scale;

    public TimedFlameProvider(SpriteSet sprites, float scale)
    {
        _sprites = sprites;
        _scale = scale;
    }

    public Particle createParticle(TimedParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        TimedFlame particle = new TimedFlame(level, x, y, z, xSpeed, ySpeed, zSpeed, options.lifetime());
        particle.pickSprite(_sprites);
        return particle.scale(_scale);
    }

    private static class TimedFlame extends FlameParticle
    {
        private TimedFlame(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int lifetime)
        {
            super(level, x, y, z, xSpeed, ySpeed, zSpeed);
            setLifetime(lifetime);
        }

        @Override public void move(double x, double y, double z)
        {
            Vec3 wanted = new Vec3(x, y, z);
            Vec3 allowed = Entity.collideBoundingBox(null, wanted, getBoundingBox(), level, List.of());

            if (allowed.x != wanted.x) xd = 0.0;
            if (allowed.y != wanted.y) yd = 0.0;
            if (allowed.z != wanted.z) zd = 0.0;

            setBoundingBox(getBoundingBox().move(allowed));
            setLocationFromBoundingbox();
        }
    }
}

package com.im.gameplay.abilities.blows;

import com.im.core.tags.Tag;
import com.im.core.values.random.RandomRange;
import com.im.visuals.particles.ParticleBand;
import com.im.visuals.particles.ParticleSet;

import java.util.List;

public record BlowParticles(Tag<ParticleSet> set, RandomRange across, RandomRange height, List<ParticleBand> bands) { }
package com.im.databases.buffs;

import com.im.gameplay.buffs.BuffGrant;
import com.im.gameplay.buffs.BuffMix;
import com.im.gameplay.buffs.BuffMixer;

public final class FoodBuffDatabase
{
    private static final BuffMixer Mixer = new BuffMixer().fuse(BuffDatabase.Bellows, BuffDatabase.SlimeBellows, BuffDatabase.StrongBellows);

    public static final BuffMix MagmaCream = BuffMix.of(new BuffGrant(BuffDatabase.Bellows, 100));
    public static final BuffMix SlimeBall = BuffMix.of(new BuffGrant(BuffDatabase.SlimeBellows, 100));
    public static final BuffMix GroundLivingEmber = BuffMix.of(new BuffGrant(BuffDatabase.EmberStrike, 1200));
    public static final BuffMix CoalDough = BuffMix.of(new BuffGrant(BuffDatabase.Nourished, 900, 8.0f));
    public static final BuffMix HalfCoalDough = CoalDough.divide(2);
    public static final BuffMix CoalBread = BuffMix.of(new BuffGrant(BuffDatabase.Nourished, 1500, 16.0f));
    public static final BuffMix CoalBun = CoalBread.divide(2);
    public static final BuffMix MagmaCreamSandwich = Mixer.mix(CoalBread, MagmaCream, MagmaCream);
    public static final BuffMix SlimeSandwich = Mixer.mix(CoalBread, SlimeBall, SlimeBall);
    public static final BuffMix EvilGoo = Mixer.mix(MagmaCream, SlimeBall);
    public static final BuffMix EvilKebab = Mixer.mix(EvilGoo, EvilGoo, EvilGoo);
    public static final BuffMix Coalburger = Mixer.mix(CoalBun, GroundLivingEmber, CoalBun);
    public static final BuffMix Slimeburger = Mixer.mix(CoalBun, SlimeBall, GroundLivingEmber, CoalBun);
    public static final BuffMix Creamburger = Mixer.mix(CoalBun, MagmaCream, GroundLivingEmber, CoalBun);
    public static final BuffMix CoalDeluxe = Mixer.mix(CoalBun, MagmaCream, GroundLivingEmber, SlimeBall, CoalBun);
}
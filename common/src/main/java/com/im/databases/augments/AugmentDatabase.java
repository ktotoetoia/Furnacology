package com.im.databases.augments;

import com.im.Furnacology;
import com.im.composition.gameplay.effects.*;
import com.im.core.lifecycle.database.Database;
import com.im.core.values.scaling.ScaleModifier;
import com.im.databases.entities.SkinDatabase;
import com.im.databases.registries.ItemRegistry;
import com.im.gameplay.augments.Augment;
import com.im.gameplay.digestion.Digestion;
import com.im.gameplay.effects.effectfactories.IEffectModifierFactory;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class AugmentDatabase
{
    public static final ResourceLocation Digester = Furnacology.id("digester");
    public static final ResourceLocation Nether = Furnacology.id("nether");
    public static final ResourceLocation End = Furnacology.id("end");
    public static final int NetherTier = 1;
    public static final int EndTier = 2;
    private static final float NetherBaseSpeed = 0.5f, EndBaseSpeed = 1.0f;
    private static final float NetherAreaBonus = 10.0f, EndAreaBonus = 15.0f;
    private static final int NetherSkinPriority = 1, EndSkinPriority = 2;

    public static final Database<Augment> Augments =new Database<Augment>()
            .add(Digester, new Augment(() -> ItemRegistry.FurnaceDigester.get(), entity -> entity.hasOfType(Digestion.class) ? 1 : 0, List.of(context -> new DigestionEffectModifier())))
            .add(Nether, Augment.single(() -> ItemRegistry.SpikyNetherite.get(), getBaseSpeed(NetherBaseSpeed), getArea(NetherAreaBonus), getTier(NetherTier),
                    getSkin(SkinDatabase.ReinforcedLivingFurnace.id(), NetherSkinPriority)))
            .add(End, Augment.single(() -> ItemRegistry.DragonChalice.get(), getBaseSpeed(EndBaseSpeed), getArea(EndAreaBonus), getTier(EndTier),
                    getSkin(SkinDatabase.EnderFurnace.id(), EndSkinPriority)));

    private static IEffectModifierFactory getBaseSpeed(float bonus) { return context -> new BaseSpeedEffectModifier(new ScaleModifier(bonus * context.potency())); }
    private static IEffectModifierFactory getArea(float bonus) { return context -> new AreaEffectModifier(new ScaleModifier(bonus * context.potency())); }
    private static IEffectModifierFactory getTier(int tier) { return context -> new TierEffectModifier(tier); }
    private static IEffectModifierFactory getSkin(ResourceLocation skin, int priority) { return context -> new SkinEffectModifier(skin, priority); }
}
package com.im.composition.initialization.factories;

import com.im.composition.gameplay.contracts.ISmeltContract;
import com.im.composition.gameplay.effects.*;
import com.im.core.lifecycle.blockentities.BlockPropertySetter;
import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.core.lifecycle.typeregistry.TypeRegistry;
import com.im.databases.augments.AugmentDatabase;
import com.im.databases.buffs.BuffDatabase;
import com.im.databases.entities.ClipDatabase;
import com.im.databases.entities.SkinDatabase;
import com.im.databases.entities.SoundDatabase;
import com.im.databases.registries.BlockRegistry;
import com.im.databases.registries.DataComponentRegistry;
import com.im.gameplay.abilities.AbilityUser;
import com.im.gameplay.abilities.blows.Blow;
import com.im.gameplay.abilities.blows.BlowPicker;
import com.im.gameplay.area.Area;
import com.im.gameplay.augments.AugmentContainer;
import com.im.gameplay.digestion.Digestion;
import com.im.gameplay.effects.containers.EffectContainer;
import com.im.gameplay.skins.FurnaceSkin;
import com.im.gameplay.skins.SkinSelector;
import com.im.gameplay.smelting.furnace.ExperienceStorage;
import com.im.gameplay.smelting.furnace.Furnace;
import com.im.gameplay.smelting.furnace.LivingFurnaceBlock;
import com.im.gameplay.smelting.memory.SmeltingMemoryWrapper;
import com.im.infrastructure.platform.Services;
import com.im.infrastructure.config.FurnacologyConfig;
import com.im.ui.hud.LivingFurnaceDocument;
import com.im.visuals.animation.animationplayer.ClipPlayer;
import com.im.visuals.audio.SoundEmitter;
import com.im.visuals.particles.ParticleEmitter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public final class LivingFurnaceFactory
{
    private static final Component Title = Component.translatable("container.furnacology.living_furnace");
    private static final Blow FallbackBlow = Blow.of(ClipDatabase.Lit, ClipDatabase.Smelt);
    private static final List<ISpawnInitializer> SpawnInitializers = new CopyOnWriteArrayList<>();

    public static void extend(ISpawnInitializer initializer) { SpawnInitializers.add(initializer); }

    public static BlockEntityType.BlockEntitySupplier<ComponentBlockEntity> blockEntity(Supplier<BlockEntityType<ComponentBlockEntity>> type)
    {
        return (pos, state) -> new ComponentBlockEntity(type.get(), pos, state, LivingFurnaceFactory::create);
    }

    private static List<Object> create(ComponentBlockEntity entity)
    {
        EffectContainer effects = new EffectContainer();
        SmeltingMemoryWrapper memory = new SmeltingMemoryWrapper(entity, FurnacologyConfig::speedCurve, DataComponentRegistry.SmeltingMemory);
        Furnace furnace = new Furnace(entity, memory::getSpeedFor, Services.Platform::getBurnTime, () -> effects.hasModifier(EjectOutputEffectModifier.class));
        Area area = new Area(entity, () -> FurnacologyConfig.AreaRadius.get().floatValue());
        SkinSelector<FurnaceSkin> skins = new SkinSelector<>(entity, SkinDatabase.FurnaceSkins, () -> effects.getModifiers(SkinEffectModifier.class));
        AbilityUser abilities = new AbilityUser(entity, BuffDatabase.Buffs::getById, FurnacologyConfig.BuffReleaseDelayTicks::get);
        Digestion digestion = new Digestion(entity, effects, abilities, BuffDatabase.Buffs::getById, BuffDatabase.FurnaceDigester, FurnacologyConfig.BuffChargeTicks::get,
                () -> effects.hasModifier(DigestionEffectModifier.class));

        effects.addObserver(new ScalingEffectObserver<>(SpeedEffectModifier.class, SpeedEffectModifier::speed, furnace.getSpeed()));
        effects.addObserver(new ScalingEffectObserver<>(BaseSpeedEffectModifier.class, BaseSpeedEffectModifier::speed, furnace.getBaseSpeed()));
        effects.addObserver(new ScalingEffectObserver<>(DoubleOutputEffectModifier.class, DoubleOutputEffectModifier::chance, furnace.getDoubleChance()));
        effects.addObserver(new ScalingEffectObserver<>(AreaEffectModifier.class, AreaEffectModifier::radius, area.getRadius()));
        effects.addObserver(new RequestSyncOnEffectChanged(entity));
        furnace.getSmelted().add(memory::record);
        furnace.getSmelted().add(item -> effects.getModifiers(ISmeltContract.class).forEach(contract -> contract.onSmelted(entity)));

        TypeRegistry<Object> parts = new TypeRegistry<>(List.of(furnace, effects, area, memory, skins, new ClipPlayer(() -> skins.getCurrent().clips()),
                new SoundEmitter(entity, () -> skins.getCurrent().sounds(), SoundDatabase::isEnabled), new ParticleEmitter(entity, () -> skins.getCurrent().particles()),
                abilities, digestion, new BlowPicker(entity, abilities::getReleaseBlows, FallbackBlow, () -> skins.getCurrent().particles()),
                new AugmentContainer(entity, effects, AugmentDatabase.Augments::getById), new ExperienceStorage(entity)));
        List<Object> components = new ArrayList<>(parts.getCollection());

        components.addAll(components.indexOf(digestion), List.of(LivingFurnaceStateMachineFactory.create(entity, parts),
                new BlockPropertySetter<>(entity, LivingFurnaceBlock.Lit, furnace::isLit), new BlockPropertySetter<>(entity, LivingFurnaceBlock.Skin, skins::getCurrentIndex),
                furnace.getItems()));
        components.add(LivingFurnaceDocument.create(entity, Title, BlockRegistry.ComponentMenuType, parts));
        SpawnInitializers.forEach(initializer -> components.addAll(initializer.initialize(entity, parts)));
        return components;
    }
}
package com.im.composition.initialization.factories;

import com.im.composition.visuals.animation.OccasionalClipAction;
import com.im.composition.visuals.animation.PlayClipAction;
import com.im.composition.visuals.audio.FrameSoundAction;
import com.im.composition.visuals.audio.LoopSoundAction;
import com.im.composition.visuals.particles.FaceParticlesAction;
import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.lifecycle.typeregistry.ITypeRegistry;
import com.im.core.statemachine.statemachine.IStateMachine;
import com.im.core.statemachine.statemachine.StateMachine;
import com.im.core.statemachine.transition.Transition;
import com.im.core.tags.Tag;
import com.im.databases.entities.ClipDatabase;
import com.im.databases.entities.SoundDatabase;
import com.im.gameplay.abilities.AbilityUser;
import com.im.gameplay.abilities.blows.BlowPicker;
import com.im.gameplay.digestion.Digestion;
import com.im.gameplay.effects.containers.EffectContainer;
import com.im.gameplay.entityactions.*;
import com.im.gameplay.smelting.furnace.ExperienceStorage;
import com.im.gameplay.smelting.furnace.Furnace;
import com.im.visuals.animation.animationplayer.ClipPlayer;
import com.im.visuals.animation.clips.Clip;
import com.im.visuals.audio.SoundEmitter;
import com.im.visuals.particles.ParticleEmitter;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class LivingFurnaceStateMachineFactory
{
    private static final float FaceSpeedMultiplier = 2.0f;
    private static final float BlowSpeed = 1.0f;
    private static final int CooldownPerTick = 1;
    private static final int IdleMinDelayTicks = 15 * 20,  IdleMaxDelayTicks = 45 * 20;
    private static final int ReleaseSoundFadeInTicks = 10, ReleaseSoundFadeOutTicks = 20;
    private static final int BiteFrame = 2, BiteFadeOutTicks = 2;
    private static final int SmeltSoundFrame = 0;
    private static final float CrumbChance = 0.15f;
    private static final double CrumbMinHeight = 0.1, CrumbMaxHeight = 0.4 , CrumbWidth = 0.6;

    public static IStateMachine create(ICompositeBlockEntity entity, ITypeRegistry<Object> parts)
    {
        Furnace furnace = parts.get(Furnace.class);
        AbilityUser abilities = parts.get(AbilityUser.class);
        BlowPicker blow = parts.get(BlowPicker.class);
        ClipPlayer face = parts.get(ClipPlayer.class);
        SoundEmitter sounds = parts.get(SoundEmitter.class);
        SmeltAction smelt = new SmeltAction(furnace, parts.get(ExperienceStorage.class));
        BuffAction buffs = new BuffAction(furnace, parts.get(EffectContainer.class), parts.get(Digestion.class), abilities);
        FaceParticlesAction crumbs = new FaceParticlesAction(parts.get(ParticleEmitter.class), entity::getFacing, () -> getItemParticle(furnace.getInputItem()),
                furnace::isSmelting, CrumbChance, CrumbMinHeight, CrumbMaxHeight, CrumbWidth);

        ActionState idle = new ActionState(new CooldownAction(furnace, CooldownPerTick), buffs, new IgniteAction(furnace),
                new OccasionalClipAction(face, ClipDatabase.Idle, ()  -> IdleMinDelayTicks, () -> IdleMaxDelayTicks));
        ActionState lit = new ActionState(smelt, buffs, playFace(face, furnace, () -> ClipDatabase.Lit, true, () -> furnace.isSmelting() || furnace.hasPendingSmelt()),
                new FrameSoundAction(face, BiteFrame, () ->  SoundDatabase.Bite, sounds, () -> 1f/*Math.clamp(furnace.getCurrentSpeed(),0f,1.5f)*/, true, BiteFadeOutTicks), crumbs);
        ActionState smelted = new ActionState(IEntityAction.runOnEnter(furnace::acknowledgeSmelt), smelt, buffs,
                playFace(face,furnace, () -> furnace.isEjecting() ? ClipDatabase.Throw : ClipDatabase.Smelt, false, () -> true),
                new FrameSoundAction(face, SmeltSoundFrame, () -> furnace.isEjecting() ? SoundDatabase.Throw : SoundDatabase.Smelt, sounds, furnace::getCurrentSpeed, false , 0),
                crumbs);
        ActionState windup = new ActionState(new WindupAction(furnace, abilities, blow, sounds),
                new PlayClipAction(face, () -> blow.getCurrent().windupAnimation(), false, () -> BlowSpeed, () -> true, () -> true));
        ActionState releasing = new ActionState( new ReleaseAction(entity, abilities),
                new LoopSoundAction(() -> blow.getCurrent().releaseSound(), sounds, ReleaseSoundFadeInTicks, ReleaseSoundFadeOutTicks),
                new PlayClipAction(face, () -> blow.getCurrent().animation(), true, () -> BlowSpeed, () -> true, () -> true ));

        addTransition(idle, lit, furnace::isLit);
        addTransition(lit, idle,() -> !furnace.isLit());
        addTransition(idle, windup, abilities::isReleaseDue);
        addTransition(lit, windup, abilities::isReleaseDue);
        addTransition(windup, releasing, face::isFinished);
        addTransition(releasing, lit, () -> abilities.isReleaseDone() && furnace.isLit());
        addTransition(releasing, idle, abilities::isReleaseDone);

        //TODO when furnace smelts animation should be changed immediately once the frame played enough time instead of waiting for whole animation just wait for a frame
        //cant break animation immediately
        addTransition(lit, smelted, ()->furnace.hasPendingSmelt() && face.isFinished());
        addTransition(smelted, idle, ()->!furnace.isLit());
        addTransition(smelted, lit, face::isFinished);

        return new StateMachine(idle);
    }

    private static PlayClipAction playFace(ClipPlayer face, Furnace furnace, Supplier<Tag<Clip>> clip, boolean loop, BooleanSupplier running)
    {
        return new PlayClipAction(face, clip, loop, () -> furnace.getCurrentSpeed() * FaceSpeedMultiplier, furnace::isLit, running);
    }

    @Nullable
    private static ParticleOptions getItemParticle(Item item) { return item == Items.AIR ? null : new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(item)); }
    private static void addTransition(ActionState from, ActionState to, BooleanSupplier condition) { from.addTransition(new Transition(from, to, condition)); }
}
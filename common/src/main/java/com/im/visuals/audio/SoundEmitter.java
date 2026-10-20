package com.im.visuals.audio;

import com.im.core.events.EventCounter;
import com.im.core.lifecycle.blockentities.ICompositeBlockEntity;
import com.im.core.tags.ITagLibrary;
import com.im.core.tags.Tag;
import com.im.infrastructure.saving.ISyncSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SoundEmitter implements ISyncSerializable
{
    private static final String TagCues = "sound_cues";
    private static final String TagCueSound = "sound_cue";
    private static final String TagCuePitch = "sound_cue_pitch";
    private static final String TagCueGroup = "sound_cue_group";
    private static final float NormalPitch = 1.0f;

    private final ICompositeBlockEntity _entity;
    private final Supplier<? extends ITagLibrary<SoundEvent>> _sounds;
    private final Predicate<Tag<SoundEvent>> _enabled;
    private final RandomSource _random = RandomSource.create();
    private final EventCounter _cues = new EventCounter();
    private ResourceLocation _cueSound;
    private float _cuePitch = NormalPitch;
    private String _cueGroup = "";

    public SoundEmitter(ICompositeBlockEntity blockEntity, Supplier<? extends ITagLibrary<SoundEvent>> sounds, Predicate<Tag<SoundEvent>> enabled)
    {
        _entity = blockEntity;
        _sounds = sounds;
        _enabled = enabled;
    }

    public void play(Tag<SoundEvent> tag, float rate, BooleanSupplier playing, int fadeOutTicks)
    {
        SoundEvent sound = _enabled.test(tag) ? _sounds.get().getRandom(tag, _random) : null;

        if (sound != null)
            getPlayer().play(sound, _entity.getBlockPos(), rate, whileLoaded(playing), fadeOutTicks);
    }

    public void loop(Tag<SoundEvent> tag, BooleanSupplier playing, int fadeInTicks, int fadeOutTicks)
    {
        SoundEvent sound = _enabled.test(tag) ? _sounds.get().getRandom(tag, _random) : null;

        if (sound != null)
            getPlayer().loop(sound, _entity.getBlockPos(), whileLoaded(playing), fadeInTicks, fadeOutTicks);
    }

    public void cue(Tag<SoundEvent> tag)
    {
        SoundEvent sound = _sounds.get().getRandom(tag, _random);

        if (sound != null)
            cue(sound, NormalPitch, tag);
    }

    public void cue(SoundEvent sound, float pitch, @Nullable Tag<SoundEvent> group)
    {
        if (!_entity.isServer())
            return;

        _cueSound = sound.getLocation();
        _cuePitch = pitch;
        _cueGroup = group == null ? "" : group.name();
        _cues.fire();
        _entity.requestSync();
    }

    public void captureSync(CompoundTag tag)
    {
        tag.putInt(TagCues, _cues.getTotal());

        if (_cueSound == null)
            return;

        tag.putString(TagCueSound, _cueSound.toString());
        tag.putFloat(TagCuePitch, _cuePitch);
        tag.putString(TagCueGroup, _cueGroup);
    }

    public void restoreSync(CompoundTag tag)
    {
        _cueSound = tag.contains(TagCueSound) ? ResourceLocation.tryParse(tag.getString(TagCueSound)) : null;
        _cuePitch = tag.contains(TagCuePitch) ? tag.getFloat(TagCuePitch) : NormalPitch;
        _cueGroup = tag.getString(TagCueGroup);
        _cues.receive(tag.getInt(TagCues));

        if (!_cues.isPending()) return;

        _cues.acknowledge();

        if (_cueSound == null ||!_cueGroup.isEmpty() && !_enabled.test(new Tag<>(_cueGroup))) return;

        getPlayer().play(SoundEvent.createVariableRangeEvent(_cueSound), _entity.getBlockPos(), _cuePitch, whileLoaded(() -> true), 0);
    }

    private ISoundPlayer getPlayer() { return SoundPlayers.forLevel(_entity.getLevel()); }
    private BooleanSupplier whileLoaded(BooleanSupplier playing) { return () -> playing.getAsBoolean() && !_entity.isRemoved(); }
}
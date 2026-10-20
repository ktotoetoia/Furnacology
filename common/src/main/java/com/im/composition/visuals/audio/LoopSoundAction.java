package com.im.composition.visuals.audio;

import com.im.core.tags.Tag;
import com.im.gameplay.entityactions.IEntityAction;
import com.im.visuals.audio.SoundEmitter;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class LoopSoundAction implements IEntityAction
{
    private final Supplier<Tag<SoundEvent>> _sound;
    private final SoundEmitter _emitter;
    private final int  _fadeInTicks;
    private final int _fadeOutTicks;
    private int  _session;
    private boolean _playing;

    public LoopSoundAction(Supplier<Tag<SoundEvent>> sound, SoundEmitter emitter, int fadeInTicks, int fadeOutTicks)
    {
        _sound = sound;
        _emitter = emitter;
        _fadeInTicks = fadeInTicks;
        _fadeOutTicks = fadeOutTicks;
    }

    @Override
    public void onEnter()
    {
        int current = ++_session;
        Tag<SoundEvent> sound = _sound.get();

        _playing = true;

        if (sound != null)
            _emitter.loop(sound, () -> _playing && _session == current, _fadeInTicks, _fadeOutTicks);
    }

    @Override
    public void onExit() { _playing = false; }
}
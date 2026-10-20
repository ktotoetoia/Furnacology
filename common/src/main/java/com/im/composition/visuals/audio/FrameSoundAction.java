package com.im.composition.visuals.audio;

import com.im.core.tags.Tag;
import com.im.gameplay.entityactions.IEntityAction;
import com.im.visuals.animation.animationplayer.IAnimationPlayer;
import com.im.visuals.audio.SoundEmitter;
import net.minecraft.sounds.SoundEvent;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class FrameSoundAction implements IEntityAction
{
    private final IAnimationPlayer _player;
    private final int _frame;
    private final Supplier<Tag<SoundEvent>> _sound;
    private final SoundEmitter _emitter;
    private final Supplier<Float> _rate;
    private final boolean _stopsOnExit;
    private final int _fadeOutTicks;
    private int _lastFrame;
    private int _session;
    private boolean _active;

    public FrameSoundAction(IAnimationPlayer player, int frame, Supplier<Tag<SoundEvent>> sound, SoundEmitter emitter, Supplier<Float> rate, boolean stopsOnExit,
                            int fadeOutTicks)
    {
        _player = player;
        _frame = frame;
        _sound = sound;
        _emitter = emitter;
        _rate = rate;
        _stopsOnExit = stopsOnExit;
        _fadeOutTicks = fadeOutTicks;
    }

    @Override
    public void onEnter()
    {
        _lastFrame = -1;
        _active = true;
        _session++;
    }

    @Override
    public void onExit() { _active = false; }

    @Override
    public void tick()
    {
        int current = _player.getFrameIndex();
        if (current == _lastFrame) return;
        _lastFrame = current;
        if (current != _frame) return;

        int visit = _session;
        BooleanSupplier playing = _stopsOnExit ? () -> _active && _session == visit : () -> true;

        _emitter.play(_sound.get(), _rate.get(), playing, _fadeOutTicks);
    }
}
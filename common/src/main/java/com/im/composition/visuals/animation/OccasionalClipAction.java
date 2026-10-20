package com.im.composition.visuals.animation;

import com.im.core.tags.Tag;
import com.im.gameplay.entityactions.IEntityAction;
import com.im.visuals.animation.animationplayer.IAnimationPlayer;
import com.im.visuals.animation.clips.Clip;
import net.minecraft.util.RandomSource;

import java.util.function.Supplier;

public class OccasionalClipAction implements IEntityAction
{
    private static final float Speed = 1.0f;

    private final IAnimationPlayer _player;
    private final Tag<Clip> _clip;
    private final Supplier<Integer> _minDelay;
    private final Supplier<Integer> _maxDelay;
    private final RandomSource _random = RandomSource.create();
    private int _countdown =  -1;
    private boolean _playing;

    public OccasionalClipAction(IAnimationPlayer player, Tag<Clip> clip, Supplier<Integer> minDelay, Supplier<Integer> maxDelay)
    {
        _player = player;
        _clip = clip;
        _minDelay = minDelay;
        _maxDelay = maxDelay;
    }

    @Override
    public void onEnter()
    {
        _playing = false;
        _countdown = -1;
    }

    @Override
    public void tick()
    {
        if (_countdown ==  -1) schedule();
        if (_playing) tickPlaying();
        else tickWaiting();
    }

    @Override
    public void onExit()
    {
        _player.stop();
        _playing = false;
    }

    private void tickPlaying()
    {
        _player.advance(Speed, true);

        if (!_player.isFinished() ) return;

        _player.stop();
        _playing = false;
        schedule();
    }

    private void tickWaiting()
    {
        if (--_countdown > 0) return;

        _player.play(_clip, false);
        _player.setVisible(true);
        _playing = true;
    }

    private void schedule()
    {
        int min = Math.max(1, _minDelay.get());
        _countdown = _random.nextIntBetweenInclusive(min, Math.max(min, _maxDelay.get()));
    }
}
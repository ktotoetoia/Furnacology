package com.im.composition.visuals.animation;

import com.im.core.tags.Tag;
import com.im.gameplay.entityactions.IEntityAction;
import com.im.visuals.animation.animationplayer.IAnimationPlayer;
import com.im.visuals.animation.clips.Clip;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class PlayClipAction implements IEntityAction
{
    private final IAnimationPlayer _player;
    private final Supplier<Tag<Clip>> _clipTag;
    private final boolean _loop;
    private final Supplier<Float> _speed;
    private final BooleanSupplier _isVisible;
    private final BooleanSupplier _isRunning;

    public PlayClipAction(IAnimationPlayer player, Supplier<Tag<Clip>> clip, boolean loop, Supplier<Float> speed, BooleanSupplier visible, BooleanSupplier running)
    {
        _player = player;
        _clipTag = clip;
        _loop = loop;
        _speed = speed;
        _isVisible = visible;
        _isRunning = running;
    }

    @Override
    public void onEnter()
    {
        _player.play(_clipTag.get(), _loop);
        _player.setVisible(_isVisible.getAsBoolean());
    }

    @Override
    public void tick()
    {
        _player.setVisible(_isVisible.getAsBoolean());
        _player.advance(_speed.get(), _isRunning.getAsBoolean());
    }

    @Override
    public void onExit() { _player.stop(); }
}
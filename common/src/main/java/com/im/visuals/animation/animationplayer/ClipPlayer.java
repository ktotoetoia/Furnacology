package com.im.visuals.animation.animationplayer;

import com.im.core.tags.ITagLibrary;
import com.im.core.tags.Tag;
import com.im.visuals.animation.clips.Clip;
import com.im.visuals.animation.clips.Frame;
import com.im.visuals.rendering.IFaceSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ClipPlayer implements IAnimationPlayer, IFaceSprite
{
    private static final float MinSpeed = 0.01f;

    private final Supplier<? extends ITagLibrary<Clip>> _library;
    private final RandomSource _random = RandomSource.create();
    private Tag<Clip> _tag;
    private ITagLibrary<Clip> _playedLibrary;
    private Clip _clip;
    private Frame _frame;
    private boolean _loop;
    private float _time;
    private boolean _finished;
    private boolean _visible;

    public ClipPlayer(Supplier<? extends ITagLibrary<Clip>> library) { _library = library; }

    public int getFrameIndex() { return _clip == null ? 0 : _clip.getIndexAt(_time); }
    public boolean isFinished() { return _finished; }
    public boolean isVisible() { return _visible && _clip != null;  }
    public void setVisible(boolean visible) { _visible = visible;  }
    public ResourceLocation getTexture() { return _frame == null ? null : _frame.strip().texture();  }
    public float getFrameHeight() { return _frame == null ? 1.0f : _frame.getHeight();}
    public float getFrameOffset() { return _frame == null ? 0.0f : _frame.getOffset();  }

    public void play(Tag<Clip> tag, boolean loop)
    {
        _tag = tag;
        _playedLibrary = _library.get();
        _clip = _playedLibrary.getRandom(tag, _random);
        _loop = loop;
        _time = 0.0f;
        _finished = _clip == null;
        refresh();
    }

    public void advance(float speed, boolean running)
    {
        if (_tag != null && _playedLibrary != _library.get()) play(_tag, _loop);
        if (_clip == null || !advanceState(running)) return;

        advanceTime(speed);
        refresh();
    }

    public void stop()
    {
        _tag = null;
        _playedLibrary = null;
        _clip = null;
        _frame = null;
        _time = 0.0f;
        _finished = false;
        _visible = false;
    }

    private boolean advanceState(boolean running)
    {
        if (_finished)
        {
            if (!_loop) return false;

            _finished = false;
        }
        if (_clip.getIndexAt(_time) == 0 && !running)
        {
            _time = 0.0f;
            refresh();
            return false;
        }

        return true;
    }

    private void advanceTime(float speed)
    {
        int index = _clip.getIndexAt(_time);
        float next = _time + Math.max(MinSpeed, speed);

        if (index + 1 < _clip.getFrameCount())
        {
            _time = Math.min(next, Math.nextDown(_clip.getEndOf(index + 1)));
        }
        else if (next < _clip.getLength())
        {
            _time = next;
        }
        else
        {
            _finished = true;
            _time = _loop ? 0.0f : _clip.getLength();

            if (_loop) _clip = _library.get().getRandom(_tag, _random);
        }
    }

    private void refresh() { _frame = _clip == null ? null : _clip.getFrameAt(_time); }
}
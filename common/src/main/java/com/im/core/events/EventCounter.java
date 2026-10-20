package com.im.core.events;

public final class EventCounter
{
    private int _total;
    private int _seen;
    private boolean _received;

    public int getTotal() { return _total; }
    public boolean isPending() { return _seen != _total; }
    public void fire() { _total++; }
    public void acknowledge() { _seen = _total; }

    public void load(int total)
    {
        _total = total;
        _seen = total;
    }
    public void receive(int total)
    {
        _total = total;

        if (_received ) return;
        _received = true;
        _seen = total;
    }
}
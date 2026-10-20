package com.im.core.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class Event<T>
{
    private final List<Consumer<T>> _handlers = new CopyOnWriteArrayList<>();

    public void add(Consumer<T> handler) { _handlers.add(handler); }
    public void remove(Consumer<T> handler) { _handlers.remove(handler); }
    public void invoke(T value) { _handlers.forEach(handler -> handler.accept(value)); }
}
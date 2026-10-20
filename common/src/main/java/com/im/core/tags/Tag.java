package com.im.core.tags;

import javax.annotation.Nullable;

public record Tag<T>(String name, @Nullable Tag<T> fallback)
{
    public Tag(String name) { this(name, null); }
}
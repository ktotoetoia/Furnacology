package com.im.core.tags;

import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.List;

public interface ITagLibrary<T>
{
    List<T> getAll(Tag<T> tag);
    @Nullable T getRandom(Tag<T> tag, RandomSource random);
}
package com.im.core.lifecycle.factory;

public interface IFactory<T, TParam>
{
    T create(TParam param);
}
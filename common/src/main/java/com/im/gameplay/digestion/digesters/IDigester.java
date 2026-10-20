package com.im.gameplay.digestion.digesters;

import com.im.gameplay.buffs.BuffGrant;

import java.util.List;

public interface IDigester<T>
{
    List<BuffGrant> digest(T input);
}

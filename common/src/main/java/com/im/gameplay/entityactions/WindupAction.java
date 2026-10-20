package com.im.gameplay.entityactions;

import com.im.core.tags.Tag;
import com.im.gameplay.abilities.AbilityUser;
import com.im.gameplay.abilities.blows.BlowPicker;
import com.im.gameplay.smelting.furnace.Furnace;
import com.im.visuals.audio.SoundEmitter;
import net.minecraft.sounds.SoundEvent;

public class WindupAction implements IEntityAction
{
    private static final float WindupRate = 1.0f;

    private final Furnace _furnace;
    private final AbilityUser _abilities;
    private final BlowPicker _blow;
    private final SoundEmitter _sounds;

    public WindupAction(Furnace furnace, AbilityUser abilities, BlowPicker blow, SoundEmitter sounds)
    {
        _furnace = furnace;
        _abilities = abilities;
        _blow = blow;
        _sounds = sounds;
    }

    @Override
    public void onEnter()
    {
        _furnace.acknowledgeSmelt();

        if (_furnace.isServer())
        {
            _abilities.beginRelease();
            _blow.pick();
            return;
        }

        Tag<SoundEvent> sound = _blow.getCurrent().windupSound();

        _abilities.acknowledgeRelease();

        if (sound != null) _sounds.play(sound, WindupRate, () -> true, 0);
    }
}
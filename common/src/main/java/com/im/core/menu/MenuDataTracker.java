package com.im.core.menu;

import net.minecraft.world.inventory.DataSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class MenuDataTracker
{
    private static final int HalfBits = 16;
    private static final int HalfMask = 0xFFFF;
    private static final float FloatScale = 1000.0f;

    private final List<SyncedInt> _values = new ArrayList<>();

    public IntSupplier track(IntSupplier source)
    {
        SyncedInt value = new SyncedInt(source);
        _values.add(value);
        return value::get;
    }

    public Supplier<Float> trackFloat(Supplier<Float> source)
    {
        IntSupplier scaled = track(() -> Math.round(source.get() * FloatScale));
        return () -> scaled.getAsInt() / FloatScale;
    }

    public void addDataSlots(Consumer<DataSlot> slots)
    {
        for (SyncedInt value : _values)
        {
            slots.accept(value.getHalf(0));
            slots.accept(value.getHalf(HalfBits));
        }
    }

    private static final class SyncedInt
    {
        private final IntSupplier _source;
        private int _value;
        private SyncedInt(IntSupplier source) { _source = source;}
        private int get() { return _value; }

        private DataSlot getHalf(int shift)
        {
            return new DataSlot()
            {
                public int get()
                {
                    _value = _source.getAsInt();
                    return (_value >>> shift) & HalfMask;
                }
                public void set(int received)
                {
                    _value = (_value & ~(HalfMask << shift)) | ((received & HalfMask) << shift);
                }
            };
        }
    }
}

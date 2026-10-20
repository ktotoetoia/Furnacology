package com.im.core.storage;

public record SideAccess(int insert, int extract)
{
    public static final int NoSlot = -1;
    public static final SideAccess None = new SideAccess(NoSlot, NoSlot);

    public static SideAccess insert(int slot) { return new SideAccess(slot, NoSlot); }
    public static SideAccess extract(int slot) { return new SideAccess(NoSlot, slot); }
}
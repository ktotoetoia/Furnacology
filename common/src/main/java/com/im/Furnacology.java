package com.im;

import com.im.databases.blockswap.BlockSwapDatabase;
import com.im.databases.entities.ParticleDatabase;
import com.im.databases.entities.SoundDatabase;
import com.im.databases.loot.LootDatabase;
import com.im.databases.registries.*;
import net.minecraft.resources.ResourceLocation;

public final class Furnacology
{
    public static final String ModId = "furnacology";

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ModId, path); }

    public static void init()
    {
        DataComponentRegistry.Components.registerAll();
        SoundDatabase.Sounds.registerAll();
        ParticleDatabase.Particles.registerAll();
        BlockRegistry.Blocks.registerAll();
        BlockRegistry.BlockEntities.registerAll();
        ItemRegistry.Items.registerAll();
        BlockRegistry.Menus.registerAll();
        CreativeTabRegistry.CreativeTabs.registerAll();
        LootDatabase.LootConditions.registerAll();
        RecipeRegistry.RecipeSerializers.registerAll();
        BlockSwapDatabase.register();
        LootDatabase.register();
    }

    private Furnacology() { }
}
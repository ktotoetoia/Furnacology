package com.im.databases.registries;

import com.im.Furnacology;
import com.im.infrastructure.platform.Registrar;
import com.im.infrastructure.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public final class CreativeTabRegistry
{
    private static final List<Registrar<Item>> Contents = new CopyOnWriteArrayList<>(List.of(ItemRegistry.Items));

    public static final Registrar<CreativeModeTab> CreativeTabs = new Registrar<>(BuiltInRegistries.CREATIVE_MODE_TAB, Furnacology.ModId);

    public static final Supplier<CreativeModeTab> MainTab = CreativeTabs.register(Furnacology.ModId, () -> Services.Platform.createCreativeTab()
            .title(Component.translatable("itemGroup." + Furnacology.ModId))
            .icon(() -> ItemRegistry.LivingFurnace.get().getDefaultInstance())
            .displayItems((parameters, output) -> Contents.forEach(items -> items.getEntries().forEach(item -> output.accept(item.get()))))
            .build());

    public static void include(Registrar<Item> items) { Contents.add(items); }
}
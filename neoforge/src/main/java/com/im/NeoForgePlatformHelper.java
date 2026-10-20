package com.im;

import com.im.infrastructure.platform.IMenuFactory;
import com.im.infrastructure.platform.IPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class NeoForgePlatformHelper implements IPlatformHelper
{
    private final Map<RegisterKey, DeferredRegister<?>> _registers = new ConcurrentHashMap<>();

    public void openMenu(ServerPlayer player, MenuProvider provider, BlockPos pos) { player.openMenu(provider, buffer -> buffer.writeBlockPos(pos)); }
    public int getBurnTime(ItemStack stack) { return stack.getBurnTime(RecipeType.SMELTING); }
    public CreativeModeTab.Builder createCreativeTab() { return CreativeModeTab.builder(); }

    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(IMenuFactory<T> factory)
    {
        return IMenuTypeExtension.create((containerId, inventory, buffer) -> factory.create(containerId, inventory, buffer.readBlockPos()));
    }

    public <T, TValue extends T> Supplier<TValue> register(Registry<T> registry, ResourceLocation id, Supplier<TValue> factory)
    {
        DeferredRegister<T> deferred = (DeferredRegister<T>) _registers.computeIfAbsent(new RegisterKey(registry.key(), id.getNamespace()), key -> create(registry, key.namespace()));
        return deferred.register(id.getPath(), factory);
    }

    private static <T> DeferredRegister<T> create(Registry<T> registry, String namespace)
    {
        DeferredRegister<T> deferred = DeferredRegister.create(registry, namespace);
        deferred.register(ModLoadingContext.get().getActiveContainer().getEventBus());
        return deferred;
    }

    private record RegisterKey(ResourceKey<? extends Registry<?>> registry, String namespace) { }
}
package com.im;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.databases.loot.LootDatabase;
import com.im.databases.registries.ItemRegistry;
import com.im.gameplay.items.IFuelItem;
import com.im.gameplay.items.IUseFirstItem;
import com.im.infrastructure.config.FurnacologyClientConfig;
import com.im.infrastructure.config.FurnacologyConfig;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public final class FurnacologyFabric implements ModInitializer
{
    public void onInitialize()
    {
        Furnacology.init();

        NeoForgeModConfigEvents.loading(Furnacology.ModId).register(FurnacologyFabric::registerFuels);
        NeoForgeModConfigEvents.reloading(Furnacology.ModId).register(FurnacologyFabric::registerFuels);
        NeoForgeConfigRegistry.INSTANCE.register(Furnacology.ModId, ModConfig.Type.COMMON, FurnacologyConfig.Spec);
        NeoForgeConfigRegistry.INSTANCE.register(Furnacology.ModId, ModConfig.Type.CLIENT, FurnacologyClientConfig.Spec);
        com.im.databases.registries.BlockRegistry.ComponentEntities.forEach(type -> ItemStorage.SIDED.registerForBlockEntity(FurnacologyFabric::getItems, type.get()));
        UseBlockCallback.EVENT.register(FurnacologyFabric::useFirst);
        LootTableEvents.MODIFY.register((key, table, source, registries) -> LootDatabase.Injector.getPoolsFor(key).forEach(table::withPool));
    }

    private static void registerFuels(ModConfig config)
    {
        if (config.getSpec() != FurnacologyConfig.Spec) return;

        for (Supplier<? extends Item> entry : ItemRegistry.Items.getEntries())
        {
            if (entry.get() instanceof IFuelItem fuel) FuelRegistry.INSTANCE.add(entry.get(), fuel.getBurnTime(new ItemStack(entry.get()), null));
        }
    }

    @Nullable
    private static Storage<ItemVariant> getItems(ComponentBlockEntity entity, @Nullable Direction side)
    {
        WorldlyContainer access = entity.getAccess();
        return access == null ? null : InventoryStorage.of(access, side);
    }

    private static InteractionResult useFirst(Player player, Level level, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isSpectator() || !(stack.getItem() instanceof IUseFirstItem item)) return InteractionResult.PASS;

        InteractionResult result = item.onItemUseFirst(stack, new UseOnContext(player, hand, hit));

        return level.isClientSide() && result != InteractionResult.PASS ? InteractionResult.SUCCESS : result;
    }
}

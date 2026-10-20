package com.im.databases.registries;

import com.im.Furnacology;
import com.im.composition.initialization.factories.LivingFurnaceFactory;
import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.core.menu.ComponentMenu;
import com.im.gameplay.smelting.furnace.LivingFurnaceBlock;
import com.im.infrastructure.platform.Registrar;
import com.im.infrastructure.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.function.Supplier;

public final class BlockRegistry
{
    private static final int LitLightLevel = 13;

    public static final Registrar<Block> Blocks = new Registrar<>(BuiltInRegistries.BLOCK, Furnacology.ModId);
    public static final Registrar<BlockEntityType<?>> BlockEntities = new Registrar<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, Furnacology.ModId);
    public static final Registrar<MenuType<?>> Menus = new Registrar<>(BuiltInRegistries.MENU, Furnacology.ModId);

    public static final Supplier<LivingFurnaceBlock> LivingFurnace = Blocks.register("living_furnace", () -> new LivingFurnaceBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(3.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
            .lightLevel(state -> state.getValue(LivingFurnaceBlock.Lit) ? LitLightLevel : 0), BlockRegistry.LivingFurnaceEntity));

    public static final Supplier<BlockEntityType<ComponentBlockEntity>> LivingFurnaceEntity = BlockEntities.register("living_furnace",
            () -> BlockEntityType.Builder.of(LivingFurnaceFactory.blockEntity(BlockRegistry.LivingFurnaceEntity), LivingFurnace.get()).build(null));

    public static final List<Supplier<BlockEntityType<ComponentBlockEntity>>> ComponentEntities = List.of(LivingFurnaceEntity);

    public static final Supplier<MenuType<ComponentMenu>> ComponentMenuType = Menus.register("component", () -> Services.Platform.createMenuType(
            (containerId, inventory, pos) -> new ComponentMenu(BlockRegistry.ComponentMenuType.get(), containerId, inventory, pos)));
}
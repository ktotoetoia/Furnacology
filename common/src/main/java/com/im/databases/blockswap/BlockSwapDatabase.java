package com.im.databases.blockswap;

import com.im.Furnacology;
import com.im.databases.registries.BlockRegistry;
import com.im.gameplay.smelting.furnace.LivingFurnaceBlock;
import com.im.infrastructure.worldgen.BlockSwapRule;
import com.im.infrastructure.worldgen.StructureBlockSwaps;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.Structure;

import static com.im.infrastructure.config.FurnacologyConfig.VillageLivingFurnaceChance;
import static com.im.infrastructure.config.FurnacologyConfig.WitchHutLivingFurnaceChance;

public final class BlockSwapDatabase
{
    public static final TagKey<Structure> WitchHuts = TagKey.create(Registries.STRUCTURE, Furnacology.id("witch_huts"));
    public static final TagKey<Structure> Villages = TagKey.create(Registries.STRUCTURE, Furnacology.id("villages"));

    public static void register()
    {
        StructureBlockSwaps.register(new BlockSwapRule(WitchHuts, state -> state.is(BlockTags.CAULDRONS), WitchHutLivingFurnaceChance::get,
                (original, openSide) -> getLivingFurnace(openSide)));
        StructureBlockSwaps.register(new BlockSwapRule(Villages, state -> state.is(Blocks.FURNACE), VillageLivingFurnaceChance::get,
                (original, openSide) -> getLivingFurnace(original.getValue(BlockStateProperties.HORIZONTAL_FACING))));
    }

    private static BlockState getLivingFurnace(Direction facing) { return BlockRegistry.LivingFurnace.get().defaultBlockState().setValue(LivingFurnaceBlock.Facing, facing); }
}

package com.im.infrastructure.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class StructureBlockSwaps
{
    private static final int HorizontalSides = 4;
    private static final List<BlockSwapRule> Rules = new ArrayList<>();
    private static final ThreadLocal<Placement> CurrentPlacement = new ThreadLocal<>();

    public static void register(BlockSwapRule rule) { Rules.add(rule); }
    public static void end() { CurrentPlacement.remove(); }

    public static void begin(WorldGenLevel level, Structure structure)
    {
        Holder<Structure> holder = level.registryAccess().registryOrThrow(Registries.STRUCTURE).wrapAsHolder(structure);
        List<BlockSwapRule> rules = Rules.stream().filter(rule -> holder.is(rule.structures())).toList();

        if (rules.isEmpty()) CurrentPlacement.remove();
        else CurrentPlacement.set(new Placement(rules, level.getSeed()));
    }

    public static BlockState swap(BlockGetter level, BlockPos pos, BlockState state)
    {
        Placement placement = CurrentPlacement.get();
        BlockSwapRule rule = placement == null ? null : placement.match(state);

        if (rule == null) return state;

        RandomSource random = placement.getRandom(pos);

        if (random.nextDouble() >= rule.chance().get()) return state;

        return rule.replacement().apply(state, getOpenSide(level, pos, random));
    }

    public static List<StructureBlockInfo> swap(List<StructureBlockInfo> infos)
    {
        Placement placement = CurrentPlacement.get();

        return placement == null ? infos : new ArrayList<>(infos.stream().map(info -> swap(placement, info)).toList());
    }

    private static StructureBlockInfo swap(Placement placement, StructureBlockInfo info)
    {
        BlockSwapRule rule = placement.match(info.state());
        RandomSource random = placement.getRandom(info.pos());

        if (rule == null || random.nextDouble() >= rule.chance().get()) return info;

        return new StructureBlockInfo(info.pos(), rule.replacement().apply(info.state(), Direction.Plane.HORIZONTAL.getRandomDirection(random)), null);
    }

    private static Direction getOpenSide(BlockGetter level, BlockPos pos, RandomSource random)
    {
        int start = random.nextInt(HorizontalSides);

        for (int offset = 0; offset < HorizontalSides; offset++)
        {
            Direction side = Direction.from2DDataValue(start + offset);
            BlockPos neighbor = pos.relative(side);

            if (level.getBlockState(neighbor).getCollisionShape(level, neighbor).isEmpty()) return side;
        }

        return Direction.from2DDataValue(start);
    }

    private record Placement(List<BlockSwapRule> rules, long seed)
    {
        private BlockSwapRule match(BlockState state) { return rules.stream().filter(rule -> rule.source().test(state)).findFirst().orElse(null); }
        private RandomSource getRandom(BlockPos pos) { return RandomSource.create(seed ^ Mth.getSeed(pos)); }
    }
}
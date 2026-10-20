package com.im.infrastructure.worldgen;

import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record BlockSwapRule(TagKey<Structure> structures, Predicate<BlockState> source, Supplier<Double> chance, BiFunction<BlockState, Direction, BlockState> replacement) { }

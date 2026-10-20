package com.im.infrastructure.mixin;

import com.im.infrastructure.worldgen.StructureBlockSwaps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin
{
    @WrapOperation(method = "placeBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean furnacology$swapBlock(WorldGenLevel level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original)
    {
        return original.call(level, pos, StructureBlockSwaps.swap(level, pos, state), flags);
    }
}

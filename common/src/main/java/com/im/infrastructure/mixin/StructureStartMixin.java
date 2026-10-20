package com.im.infrastructure.mixin;

import com.im.infrastructure.worldgen.StructureBlockSwaps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureStart.class)
public abstract class StructureStartMixin
{
    @Shadow @Final private Structure structure;

    @Inject(method = "placeInChunk", at = @At("HEAD"))
    private void furnacology$beginSwaps(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random,
                                        BoundingBox box, ChunkPos chunkPos, CallbackInfo callback)
    {
        StructureBlockSwaps.begin(level, structure);
    }

    @Inject(method = "placeInChunk", at = @At("RETURN"))
    private void furnacology$endSwaps(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random,
                                      BoundingBox box, ChunkPos chunkPos, CallbackInfo callback)
    {
        StructureBlockSwaps.end();
    }
}

package com.im.infrastructure.mixin;

import com.im.infrastructure.worldgen.StructureBlockSwaps;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin
{
    @Inject(method = "processBlockInfos*", at = @At("RETURN"), cancellable = true)
    private static void furnacology$swapBlocks(CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> callback)
    {
        callback.setReturnValue(StructureBlockSwaps.swap(callback.getReturnValue()));
    }
}

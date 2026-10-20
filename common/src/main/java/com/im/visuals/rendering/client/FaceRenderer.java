package com.im.visuals.rendering.client;

import com.im.core.lifecycle.blockentities.ComponentBlockEntity;
import com.im.visuals.rendering.IFaceSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class FaceRenderer implements BlockEntityRenderer<ComponentBlockEntity>
{
    private static final float LayerOffset = 0.001f;
    private static final float[][] CornerUvs = { { 0.0f, 0.0f }, { 0.0f, 1.0f }, { 1.0f, 1.0f }, { 1.0f, 0.0f } };
    private static final int VertexSize = 8;
    private static final int Position = 0;
    private static final int Color = 3;
    private static final int Uv = 4;
    private static final int Normal = 7;
    private static final float NormalScale = 127.0f;
    private static final int ByteMask = 0xFF;

    private final ModelBlockRenderer _modelRenderer;
    private final RandomSource _random = RandomSource.create();

    public FaceRenderer(BlockEntityRendererProvider.Context context) { _modelRenderer = context.getBlockRenderDispatcher().getModelRenderer(); }

    public void render(ComponentBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay)
    {
        Level level = entity.getLevel();
        List<IFaceSprite> sprites = entity.getAll(IFaceSprite.class);
        if (level == null || sprites.isEmpty()) return;
        Direction facing = entity.getFacing();
        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.cutoutMipped());
        float offset = 0.0f;

        for (IFaceSprite face : sprites)
        {
            if (!face.isVisible()) continue;

            offset += LayerOffset;
            BakedModel model = new FaceModel(bake(atlas.getSprite(face.getTexture()), face, facing, offset));

            poseStack.pushPose();
            _modelRenderer.tesselateBlock(level, model, entity.getBlockState(), entity.getBlockPos(), poseStack, buffer, false, _random, 0, packedOverlay);
            poseStack.popPose();
        }
    }

    private static BakedQuad bake(TextureAtlasSprite sprite, IFaceSprite face, Direction facing, float offset)
    {
        float[] bounds = getBounds(facing, offset);
        FaceInfo corners = FaceInfo.fromFacing(facing);
        int normal = packNormal(facing.getNormal());

        int[] vertices = new int[CornerUvs.length * VertexSize];

        for (int corner = 0; corner < CornerUvs.length; corner++)
        {
            FaceInfo.VertexInfo vertex = corners.getVertexInfo(corner);
            int start = corner * VertexSize;

            vertices[start + Position] = Float.floatToRawIntBits(bounds[vertex.xFace]);
            vertices[start + Position +  1] = Float.floatToRawIntBits(bounds[vertex.yFace]);
            vertices[start + Position +  2] = Float.floatToRawIntBits(bounds[vertex.zFace]);
            vertices[start + Color] = -1;
            vertices[start + Uv] = Float.floatToRawIntBits(sprite.getU(CornerUvs[corner][0]));
            vertices[start + Uv +  1] = Float.floatToRawIntBits(sprite.getV(face.getFrameOffset()+face.getFrameHeight()*CornerUvs[corner][1]));
            vertices[start + Normal] = normal;
        }
        return new BakedQuad(vertices, -1, facing, sprite, true);
    }

    private static int packNormal(Vec3i normal)
    {
        return (byte) (normal.getX() * NormalScale) & ByteMask
                | ((byte) (normal.getY() * NormalScale) & ByteMask) << 8
                | ((byte) (normal.getZ() * NormalScale) & ByteMask) << 16;
    }

    private static float[] getBounds(Direction facing, float offset)
    {
        float[] bounds = new float[Direction.values().length];

        for (Direction side : Direction.values())
        {
            boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            float edge = positive ? 1.0f : 0.0f;
            bounds[side.get3DDataValue()] = side != facing ? edge : positive ? edge + offset : edge - offset;
        }

        return bounds;
    }

    private record FaceModel(BakedQuad quad) implements BakedModel
    {
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) { return side == null ? List.of(quad) : List.of(); }
        public boolean useAmbientOcclusion() { return true; }
        public boolean isGui3d() { return false; }
        public boolean usesBlockLight() { return true; }
        public boolean isCustomRenderer() { return false; }
        public TextureAtlasSprite getParticleIcon() { return quad.getSprite(); }
        public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
        public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
    }
}
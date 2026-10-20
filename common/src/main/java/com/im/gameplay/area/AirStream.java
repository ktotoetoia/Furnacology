package com.im.gameplay.area;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

//ai generated
public final class AirStream
{
    private static final double[] RayOffsets = { -0.25, 0.0, 0.25 };
    private static final double RayMargin = 1.0 / 32.0;
    private static final double HalfBlock = 0.5;

    private final BlockPos _origin;
    private final Direction _facing;
    private final Vec3 _out;
    private final Vec3 _side;
    private final Vec3 _face;
    private final double _halfWidth;
    private final double _height;
    private final int _lanes;
    private final double[][] _lengths;
    private final List<AABB> _regions = new ArrayList<>();

    private AirStream(BlockPos origin, Direction facing, double width, double height)
    {
        _origin = origin;
        _facing = facing;
        _out = Vec3.atLowerCornerOf(facing.getNormal());
        _side = Vec3.atLowerCornerOf(facing.getClockWise().getNormal());
        _face = Vec3.atCenterOf(origin).add(_out.scale(HalfBlock));
        _halfWidth = width / 2.0;
        _height = height;
        _lanes = (int) Math.ceil(_halfWidth - HalfBlock);
        _lengths = new double[_lanes * 2 + 1][(int) Math.ceil(height)];
    }

    public static AirStream trace(Level level, BlockPos origin, Direction facing, double range, double width, double height)
    {
        AirStream stream = new AirStream(origin, facing, width, height);
        stream.fill(level, range);
        return stream;
    }

    public boolean isEmpty() { return _regions.isEmpty(); }
    public boolean touches(AABB box) { return _regions.stream().anyMatch(region -> region.intersects(box)); }
    public AABB getBounds() { return _regions.stream().reduce(_regions.getFirst(), AABB::minmax); }

    public double getReach(Vec3 point)
    {
        int lane = Mth.clamp((int) Math.round(point.subtract(_face).dot(_side)), -_lanes, _lanes);
        int row = Mth.clamp(Mth.floor(point.y - _origin.getY()), 0, _lengths[0].length - 1);

        return getLength(lane, row);
    }

    private double getLength(int lane, int row) { return _lengths[lane + _lanes][row]; }

    private void fill(Level level, double range)
    {
        for (int ring = 0; ring <= _lanes; ring++)
        {
            for (int lane : ring == 0 ? new int[] { 0 } : new int[] { ring, -ring })
            {
                for (int row = 0; row < _lengths[0].length; row++)
                {
                    double limit = range;

                    if (row > 0) limit = Math.min(limit, getLength(lane, row - 1));
                    if (lane != 0) limit = Math.min(limit, getLength(lane - Integer.signum(lane), row));

                    double length = getClearLength(level, lane, row, limit);
                    _lengths[lane + _lanes][row] = length;

                    if (length > 0.0) _regions.add(getRegion(lane, row, length));
                }
            }
        }
    }

    private double getClearLength(Level level, int lane, int row, double limit)
    {
        for (int step = 1; step - 1 < limit; step++)
        {
            BlockPos cell = _origin.relative(_facing, step).relative(_facing.getClockWise(), lane).above(row);

            if (!level.isLoaded(cell)) return Math.min(limit, step - 1);

            VoxelShape shape = level.getBlockState(cell).getCollisionShape(level, cell);

            if (shape.isEmpty()) continue;

            double hit = getHitDistance(shape, cell);

            if (hit < Double.MAX_VALUE) return Mth.clamp(hit, 0.0, limit);
        }

        return limit;
    }

    private double getHitDistance(VoxelShape shape, BlockPos cell)
    {
        Vec3 entry = Vec3.atCenterOf(cell).subtract(_out.scale(HalfBlock + RayMargin));
        Vec3 through = _out.scale(1.0 + RayMargin * 2.0);
        double nearest = Double.MAX_VALUE;

        for (double across : RayOffsets)
        {
            for (double up : RayOffsets)
            {
                Vec3 start = entry.add(_side.scale(across)).add(0.0, up, 0.0);
                BlockHitResult hit = shape.clip(start, start.add(through), cell);

                if (hit != null) nearest = Math.min(nearest, hit.getLocation().subtract(_face).dot(_out));
            }
        }

        return nearest;
    }

    private AABB getRegion(int lane, int row, double length)
    {
        double left = Math.max(lane - HalfBlock, -_halfWidth);
        double right = Math.min(lane + HalfBlock, _halfWidth);
        double bottom = _origin.getY() + row;
        double top = Math.min(bottom + 1.0, _origin.getY() + _height);
        Vec3 near = _face.add(_side.scale(left));
        Vec3 far = _face.add(_side.scale(right)).add(_out.scale(length));

        return new AABB(near.x, bottom, near.z, far.x, top, far.z);
    }
}

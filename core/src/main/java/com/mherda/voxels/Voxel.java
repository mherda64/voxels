package com.mherda.voxels;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Voxel {

    VoxelType type;
    BoundingBox box;

    Vec3 localPos;
    Vec3 worldPos;

    public Voxel(int x, int y, int z, VoxelType type, Vec3 offset) {
        this.localPos = new Vec3(x, y, z);
        this.worldPos = new Vec3(x + offset.x, y + offset.y, z + offset.z);
        this.type = type;
        this.box = new BoundingBox(
            new Vector3(worldPos.x, worldPos.y, worldPos.z),
            new Vector3(worldPos.x + 1, worldPos.y + 1, worldPos.z + 1)
        );
    }

    public boolean isNone() {
        return type.isNone();
    }

    public VoxelType getType() {
        return type;
    }

    public void setType(VoxelType type) {
        this.type = type;
    }

    public BoundingBox getBox() {
        return box;
    }

    public void setBox(BoundingBox box) {
        this.box = box;
    }
}

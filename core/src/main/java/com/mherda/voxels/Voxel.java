package com.mherda.voxels;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Voxel {

    VoxelType type;
    BoundingBox box;

    int x, y, z;

    public Voxel(VoxelType type, BoundingBox box) {
        this.type = type;
        this.box = box;
    }

    public Voxel(int x, int y, int z, VoxelType type, BoundingBox box) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.box = box;
    }

    public Vector3 getPos() {
        return new Vector3(x, y, z);
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

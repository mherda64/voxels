package com.mherda.voxels;

import com.badlogic.gdx.math.Vector3;

public class Chunk {

    final VoxelType[] voxels;

    final int sX;
    final int sY;
    final int sZ;
    final int widthTimesHeight;
    final Vector3 offset;

    final int nextTopOffset;
    final int nextBottomOffset;
    final int nextFrontOffset;
    final int nextBackOffset;
    final int nextLeftOffset;
    final int nextRightOffset;

    public Chunk(int sX, int sY, int sZ, Vector3 offset) {
        this.sX = sX;
        this.sY = sY;
        this.sZ = sZ;
        this.widthTimesHeight = sX * sY;
        this.offset = offset;

        voxels = new VoxelType[sX * sY * sZ];

        nextTopOffset = sX * sZ;
        nextBottomOffset = -sX * sZ;
        nextFrontOffset = sX;
        nextBackOffset = -sX;
        nextLeftOffset = -1;
        nextRightOffset = 1;
    }

    public VoxelType get(int x, int y, int z) {
        if (x < 0 || x >= sX) return VoxelType.NONE;
        if (y < 0 || y >= sY) return VoxelType.NONE;
        if (z < 0 || z >= sZ) return VoxelType.NONE;
        return getFast(x, y, z);
    }

    public VoxelType getFast(int x, int y, int z) {
        return voxels[x + z * sX + y * widthTimesHeight];
    }

    public void set(int x, int y, int z, VoxelType type) {
        if (x < 0 || x >= sX) return;
        if (y < 0 || y >= sY) return;
        if (z < 0 || z >= sZ) return;
        setFast(x, y, z, type);
    }

    public void setFast(int x, int y, int z, VoxelType type) {
        voxels[x + z * sX + y * widthTimesHeight] = type;
    }

    public int calculateVertices(float[] vertices) {
        int voxelIndex = 0;
        int vertexOffset = 0;
        for (int y = 0; y < sY; y++) {
            for (int z = 0; z < sZ; z++) {
                for (int x = 0; x < sX; x++, voxelIndex++) {
                    if (voxels[voxelIndex].isNone()) continue;

                    if (y < sY - 1) {
                        if (voxels[voxelIndex + nextTopOffset].isNone())
                            vertexOffset = createTop(x, y, z, offset, vertices, vertexOffset);
                    } else
                        vertexOffset = createTop(x, y, z, offset, vertices, vertexOffset);

                    if (y > 0) {
                        if (voxels[voxelIndex + nextBottomOffset].isNone())
                            vertexOffset = createBottom(x, y, z, offset, vertices, vertexOffset);
                    } else
                        vertexOffset = createBottom(x, y, z, offset, vertices, vertexOffset);

                    if (x < sX - 1) {
                        if (voxels[voxelIndex + nextRightOffset].isNone()) {
//                            createRight()
                        }
                    } else {
//                        createRight
                    }

                    if (x > 0) {
                        if (voxels[voxelIndex + nextLeftOffset].isNone()) {
//                            createLeft()
                        }
                    } else {
//                        createLeft()
                    }

                    if (z < sZ - 1) {
                        if (voxels[voxelIndex + nextBackOffset].isNone()) {
//                            createBack()
                        }
                    } else {
//                        createBack()
                    }

                    if (z > 0) {
                        if (voxels[voxelIndex + nextFrontOffset].isNone()) {
//                            createFront()
                        }
                    } else {
//                        createFront()
                    }
                }
            }
        }

        return 0;
    }

    private int createTop(int x, int y, int z, Vector3 offset, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    private int createBottom(int x, int y, int z, Vector3 offset, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }
}

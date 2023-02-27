package com.mherda.voxels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Chunk implements RenderableProvider {

    private final int VERTEX_SIZE = 6;

    final Voxel[] voxels;

    final float[] vertices;
    int recentVerticesCount = 0;

    final Mesh mesh;

    final BoundingBox box;

    final Material material;

    boolean dirty = true;

    final int sX;
    final int sY;
    final int sZ;
    final int widthTimesHeight;
    final Vec3 offset;

    final int nextTopOffset;
    final int nextBottomOffset;
    final int nextFrontOffset;
    final int nextBackOffset;
    final int nextLeftOffset;
    final int nextRightOffset;

    public Chunk(int sX, int sY, int sZ, Vec3 offset) {
        this.sX = sX;
        this.sY = sY;
        this.sZ = sZ;
        this.widthTimesHeight = sX * sY;
        this.offset = offset;

        voxels = new Voxel[sX * sY * sZ];

        for (int z = 0, i = 0; z < sZ; z++) {
            for (int y = 0; y < sY; y++) {
                for (int x = 0; x < sX; x++) {
                    voxels[i++] = new Voxel(
                        x,
                        y,
                        z,
                        VoxelType.NONE,
                        offset
                    );
                }
            }
        }

        nextTopOffset = sX * sZ;
        nextBottomOffset = -sX * sZ;
        nextFrontOffset = -sX;
        nextBackOffset = sX;
        nextLeftOffset = -1;
        nextRightOffset = 1;

        material = new Material(new ColorAttribute(ColorAttribute.Diffuse,
            MathUtils.random(0.5f, 1f),
            MathUtils.random(0.5f, 1f),
            MathUtils.random(0.5f, 1f), 1)
        );

        box = new BoundingBox(
            new Vector3(offset.x, offset.y, offset.z),
            new Vector3(offset.x + sX, offset.y + sY, offset.z + sZ)
        );

        // Create indicies for the chunk mesh
        int len = sX * sY * sZ * VERTEX_SIZE * 2;
        short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            // First triangle
            indices[i] = j;
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);
            // Second triangle
            indices[i + 3] = (short) (j + 2);
            indices[i + 4] = (short) (j + 3);
            indices[i + 5] = j;
        }

        mesh = new Mesh(true,
            sX * sY * sZ * VERTEX_SIZE * 4 * 4,
            sX * sY * sZ * VERTEX_SIZE * 6 * 6,
            VertexAttribute.Position(),
            VertexAttribute.Normal());
        mesh.setIndices(indices);

//        4 Vertices, 6 faces, x * y * z blocks
        vertices = new float[VERTEX_SIZE * 4 * 6 * sX * sY * sZ];
    }

    public Voxel get(int x, int y, int z) {
        if (x < 0 || x >= sX) throw new IllegalStateException(String.format("Tried to access (%d, %d, %d) voxel in chunk", x, y, z));
        if (y < 0 || y >= sY) throw new IllegalStateException(String.format("Tried to access (%d, %d, %d) voxel in chunk", x, y, z));
        if (z < 0 || z >= sZ) throw new IllegalStateException(String.format("Tried to access (%d, %d, %d) voxel in chunk", x, y, z));
        return getFast(x, y, z);
    }

    public Voxel getFast(int x, int y, int z) {
        return voxels[x + z * sX + y * widthTimesHeight];
    }

    public Voxel[] getVoxels() {
        return voxels;
    }

    public void set(int x, int y, int z, VoxelType type) {
        if (x < 0 || x > sX) throw new IllegalStateException(String.format("Tried to access (%d, %d, %d) voxel in chunk", x, y, z));
        if (y < 0 || y > sY) throw new IllegalStateException(String.format("Tried to access (%d, %d, %d) voxel in chunk", x, y, z));
        if (z < 0 || z > sZ) throw new IllegalStateException(String.format("Tried to access (%d, %d, %d) voxel in chunk", x, y, z));
        setFast(x, y, z, type);
        setDirty(true);
    }

    public void setFast(int x, int y, int z, VoxelType type) {
        voxels[x + z * sX + y * widthTimesHeight].setType(type);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int calculateVertices(float[] vertices) {
        int voxelIndex = 0;
        int vertexOffset = 0;
        for (int y = 0; y < sY; y++) {
            for (int z = 0; z < sZ; z++) {
                for (int x = 0; x < sX; x++, voxelIndex++) {
                    if (voxels[voxelIndex].isNone()) continue;

                    if (y > 0) {
                        if (voxels[voxelIndex + nextBottomOffset].isNone())
                            vertexOffset = createBottom(x, y, z, offset, vertices, vertexOffset);
                    } else
                        vertexOffset = createBottom(x, y, z, offset, vertices, vertexOffset);


                    if (y < sY - 1) {
                        if (voxels[voxelIndex + nextTopOffset].isNone())
                            vertexOffset = createTop(x, y, z, offset, vertices, vertexOffset);
                    } else
                        vertexOffset = createTop(x, y, z, offset, vertices, vertexOffset);

                    if (x > 0) {
                        if (voxels[voxelIndex + nextLeftOffset].isNone())
                            vertexOffset = createLeft(x, y, z, offset, vertices, vertexOffset);
                    } else
                        vertexOffset = createLeft(x, y, z, offset, vertices, vertexOffset);


                    if (x < sX - 1) {
                        if (voxels[voxelIndex + nextRightOffset].isNone())
                            vertexOffset = createRight(x, y, z, offset, vertices, vertexOffset);
                    } else
                        vertexOffset = createRight(x, y, z, offset, vertices, vertexOffset);

                    if (z > 0) {
                        if (voxels[voxelIndex + nextFrontOffset].isNone()) {
                            vertexOffset = createFront(x, y, z, offset, vertices, vertexOffset);
                        }
                    } else
                        vertexOffset = createFront(x, y, z, offset, vertices, vertexOffset);

                    if (z < sZ - 1) {
                        if (voxels[voxelIndex + nextBackOffset].isNone())
                            vertexOffset = createBack(x, y, z, offset, vertices, vertexOffset);
                    } else
                        vertexOffset = createBack(x, y, z, offset, vertices, vertexOffset);
                }
            }
        }

        return vertexOffset / VERTEX_SIZE;
    }

    private int createTop(int x, int y, int z, Vec3 offset, float[] vertices, int vertexOffset) {
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

    private int createBottom(int x, int y, int z, Vec3 offset, float[] vertices, int vertexOffset) {
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

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public static int createLeft(int x, int y, int z, Vec3 offset, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public static int createRight(int x, int y, int z, Vec3 offset, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public static int createFront(int x, int y, int z, Vec3 offset, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        return vertexOffset;
    }

    public static int createBack(int x, int y, int z, Vec3 offset, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        return vertexOffset;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if (isDirty()) {
            recentVerticesCount = calculateVertices(vertices);
//            Gdx.app.log("vertCount", String.valueOf(recentVerticesCount));
            setDirty(false);
            mesh.setVertices(vertices, 0, recentVerticesCount / 4 * 6 * VERTEX_SIZE);
        }

        if (recentVerticesCount != 0) {
            Renderable renderable = pool.obtain();
            renderable.material = material;
            renderable.meshPart.mesh = mesh;
            renderable.meshPart.offset = 0;
            renderable.meshPart.size = recentVerticesCount / 4 * 6 * VERTEX_SIZE;
            renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
            renderables.add(renderable);
        }
    }

    public void setFull(VoxelType type) {
        for (int x = 0; x < sX; x++) {
            for (int y = 0; y < sY; y++) {
                for (int z = 0; z < sZ; z++) {
                    set(x, y, z, type);
                }
            }
        }
    }
}

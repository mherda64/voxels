package com.mherda.voxels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


public class World {

    private final int CHUNK_WIDTH = 4;
    private final int CHUNK_HEIGHT = 4;
    private final int WORLD_SIZE = 2;

    Chunk[] chunks;

    public World() {
        chunks = new Chunk[WORLD_SIZE * WORLD_SIZE];

        for (int z = 0, i = 0; z < WORLD_SIZE; z++) {
            for (int x = 0; x < WORLD_SIZE; x++) {
                chunks[i++] = new Chunk(
                    CHUNK_WIDTH,
                    CHUNK_HEIGHT,
                    CHUNK_WIDTH,
                    new Vector3(x * CHUNK_WIDTH, 0, z * CHUNK_WIDTH)
                );
            }
        }
    }

    public Chunk[] getChunks() {
        return chunks;
    }

    public void randomize(int randomBlocks) {
        for (Chunk chunk : chunks) {
            for (int i = 0; i < randomBlocks; i++) {
                chunk.set(
                    MathUtils.random(CHUNK_WIDTH - 1),
                    MathUtils.random(CHUNK_HEIGHT - 1),
                    MathUtils.random(CHUNK_WIDTH - 1),
                    VoxelType.BLOCK
                );
            }
        }
    }

    public void setFull() {
        for (Chunk chunk: chunks) {
            for (int x = 0; x < chunk.sX; x++) {
                for (int y = 0; y < chunk.sY; y++) {
                    for (int z = 0; z < chunk.sZ; z++) {
                        chunk.set(x, y, z, VoxelType.BLOCK);
                    }
                }
            }
        }
    }

    public void set(int x, int y, int z, VoxelType type) {
        chunks[x / CHUNK_WIDTH + z / CHUNK_WIDTH * WORLD_SIZE].set(x % CHUNK_WIDTH, y % CHUNK_HEIGHT, z % CHUNK_WIDTH, type);
    }

    public Voxel get(int x, int y, int z) {
        return chunks[x / CHUNK_WIDTH + z / CHUNK_WIDTH * WORLD_SIZE].get(x % CHUNK_WIDTH, y % CHUNK_HEIGHT, z % CHUNK_WIDTH);
    }
}

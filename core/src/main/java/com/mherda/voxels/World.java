package com.mherda.voxels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


public class World {

    private final int CHUNK_WIDTH = 16;
    private final int CHUNK_HEIGHT = 32;
    private final int WORLD_SIZE = 4;

    Chunk[] chunks;

    public World() {
        chunks = new Chunk[WORLD_SIZE * WORLD_SIZE];

        for (int x = 0, i = 0; x < WORLD_SIZE; x++) {
            for (int z = 0; z < WORLD_SIZE; z++) {
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
}

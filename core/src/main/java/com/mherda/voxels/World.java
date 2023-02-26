package com.mherda.voxels;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


public class World {

    private final int CHUNK_WIDTH = 16;
    private final int CHUNK_HEIGHT = 16;

    Chunk chunk;

    public World() {
        chunk = new Chunk(CHUNK_WIDTH, CHUNK_WIDTH, CHUNK_HEIGHT, new Vector3(0f, 0f, 0f));
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void randomize(int randomBlocks) {
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

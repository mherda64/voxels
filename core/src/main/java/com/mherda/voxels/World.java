package com.mherda.voxels;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class World {

    private final int CHUNK_WIDTH = 16;
    private final int CHUNK_HEIGHT = 64;

    Chunk chunk;

    public World() {
        chunk = new Chunk(CHUNK_WIDTH, CHUNK_WIDTH, CHUNK_HEIGHT, new Vector3());
    }

    public Array<RenderableProvider> getRenderable() {
        Array<RenderableProvider> renderable = new Array();
        renderable.add(chunk);
        return renderable;
    }

    public void randomize() {
        for (int y = 0; y < 32; y++) {
            for (int z = 0; z < 32; z++) {
                for (int x = 0; x < 32; x++) {
                    chunk.set(x % CHUNK_WIDTH, y % CHUNK_HEIGHT, z % CHUNK_WIDTH, VoxelType.BLOCK);
                }
            }
        }
    }
}

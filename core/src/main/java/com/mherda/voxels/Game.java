package com.mherda.voxels;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {
    ModelBatch modelBatch;
    PerspectiveCamera camera;
    Environment lights;
    FirstPersonCameraController controller;
    World world;
    Chunk[] chunks;

    @Override
    public void create() {
        DefaultShader.Config config = new DefaultShader.Config();
        config.defaultCullFace = GL20.GL_FRONT;
        DefaultShaderProvider provider = new DefaultShaderProvider(config);
        modelBatch = new ModelBatch(provider);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 500;
        controller = new FirstPersonCameraController(camera);
//        controller.setVelocity(50f);
        Gdx.input.setInputProcessor(controller);

        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1.f));
        lights.add(new DirectionalLight().set(1, 1, 1, 0, -1f, 0));
        lights.add(new DirectionalLight().set(1, 1, 1, 0, 0, 1f));

        world = new World();
//        world.randomize(1024);
        world.setFull();
        chunks = world.getChunks();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.0f, 0.3f, 0.6f, 1f, true);
        modelBatch.begin(camera);
        for (int i = 0; i < chunks.length; i++)
            modelBatch.render(chunks[i], lights);
        modelBatch.end();

        controller.update();
        camera.update();

        Ray pickRay = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        for (Chunk chunk : chunks) {
            if (Intersector.intersectRayBoundsFast(pickRay, chunk.box)) {
                Gdx.app.log("Pointing at chunk", String.format("%s", chunk));
//                chunk.setFull(VoxelType.NONE);
                for (Voxel voxel : chunk.getVoxels()) {
                    if (Intersector.intersectRayBoundsFast(pickRay, voxel.box)) {
                        Vec3 voxelPos = voxel.worldPos;
                        // TODO Calc dist from center of the voxel
                        double dist = Math.sqrt(
                            (camera.position.x - voxelPos.x) * (camera.position.x - voxelPos.x) +
                                (camera.position.y - voxelPos.y) * (camera.position.y - voxelPos.y) +
                                (camera.position.z - voxelPos.z) * (camera.position.z - voxelPos.z)
                        );
                        Gdx.app.log("dist", String.format("%f %d %d %d", dist, voxelPos.x, voxelPos.y, voxelPos.z));

                        if (dist < 1) {
                            chunk.set(voxel.localPos.x, voxel.localPos.y, voxel.localPos.z, VoxelType.NONE);
                        }
                    }
                }

            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
}

package com.mherda.voxels;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Intersector;
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
        controller.setVelocity(50f);
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
        Gdx.app.log("ray", pickRay.toString());

        for (Chunk chunk : chunks) {
            for (Voxel voxel : chunk.getVoxels()) {
                if (!voxel.isNone() && Intersector.intersectRayBoundsFast(pickRay, voxel.box)) {
                    Gdx.app.log("removed", String.format("%d %d %d", voxel.x, voxel.y, voxel.z));
                    chunk.set(voxel.x, voxel.y, voxel.z, VoxelType.NONE);
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

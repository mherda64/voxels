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
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {
    ModelBatch modelBatch;
    PerspectiveCamera camera;
    Environment lights;
    FirstPersonCameraController controller;

    World world;

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
        lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        lights.add(new DirectionalLight().set(1, 1, 1, -0.8f, 0, 0));

        world = new World();
        Chunk chunk = world.chunk;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (y == 0 || y == 15 || x == 0 || x == 15)
                        chunk.set(x, y, z, VoxelType.BLOCK);
                }
            }
        }
//        world.randomize(512);

//        for (int x = 15; x < 16; x++) {
//            for (int y = 15; y < 16; y++) {
//                for (int z = 15; z < 16; z++) {
//                    world.chunk.set(x, y, z, VoxelType.BLOCK);
//                }
//            }
//        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.0f, 0.3f, 0.6f, 1f, true);
        modelBatch.begin(camera);
        modelBatch.render(world.getChunk(), lights);
        modelBatch.end();
        controller.update();
    }

    @Override
    public void resize (int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }
}

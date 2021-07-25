package engine.entities;

import engine.Loader;
import engine.graphics.Texture;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TextureShader;
import org.joml.Vector3f;

public class TestEntity extends Entity {

    public TestEntity(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);

        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
        };
        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };
        float[] textureCoords = {
                0,0,
                0,1,
                1,1,
                1,0
        };

        this.model = Loader.loadToVAO(vertices, textureCoords, indices);
        Texture texture = Loader.loadTexture("stone.png");
        this.model.setTexture(texture);
        this.model = Loader.loadModel("stall.obj", "stallTexture.png");
        this.shader = new TextureShader();
    }

}

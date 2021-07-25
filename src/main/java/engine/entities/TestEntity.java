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
        this.model = Loader.loadModel("Earth 2K.obj", "Diffuse_2K.png");
        this.shader = new TextureShader();
    }

}

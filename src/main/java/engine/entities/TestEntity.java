package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TextureShader;
import org.joml.Vector3f;

public class TestEntity extends Entity {

    public TestEntity(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel(this,"cube.obj", "");
        this.material = new Material(new Vector3f(1), new Vector3f(1));
    }

    @Override
    public boolean isColliding(Vector3f checkedPosition) {
        return false;
    }

}

package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import engine.graphics.shaders.TextureShader;
import org.joml.Vector3f;

public class Pine extends Entity {

    public Pine(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel("pine.obj", "pine.png");
        this.shader = new TextureShader();
        this.material = new Material(new Vector3f(1), new Vector3f(1));
    }

}

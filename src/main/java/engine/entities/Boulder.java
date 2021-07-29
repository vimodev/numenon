package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import engine.graphics.shaders.TextureShader;
import org.joml.Vector3f;

public class Boulder extends Entity {

    public Boulder(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel("boulder.obj", "boulder.png");
        this.shader = new TextureShader();
        this.material = new Material(new Vector3f(1), new Vector3f(1));
    }

    @Override
    public boolean isColliding(Vector3f checkedPosition) {
        float distance = position.distance(checkedPosition);
        float horizontalScale = Math.max(scale.x, scale.z);
        return (distance < horizontalScale * 10f);
    }


}

package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import engine.graphics.shaders.TextureShader;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Pine extends Entity {

    public Pine(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel("pine.obj", "pine.png");
        this.shader = new TextureShader();
        this.material = new Material(new Vector3f(1), new Vector3f(1));
    }

    @Override
    public boolean isColliding(Vector3f checkedPosition) {
        float horizontalDistance = new Vector2f(position.x, position.z).distance(checkedPosition.x, checkedPosition.z);
        float horizontalScale = Math.max(scale.x, scale.z);
        return (horizontalDistance < horizontalScale * 3f);
    }

}

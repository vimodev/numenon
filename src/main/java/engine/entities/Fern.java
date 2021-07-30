package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Fern extends Entity {

    public Fern(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel(this,"fern.obj", "fern.png");
        this.material = new Material(new Vector3f(1), new Vector3f(1));
        this.hasTransparency = true;
    }

    @Override
    public boolean isColliding(Vector3f checkedPosition) {
        // No collisions for this entity
        return false;
    }

}

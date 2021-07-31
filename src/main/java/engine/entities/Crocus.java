package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import org.joml.Vector3f;

public class Crocus extends Entity {

    public Crocus(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel(this,"crocus.obj", "crocus.png");
        this.material = new Material(new Vector3f(1), new Vector3f(1));
    }

    @Override
    public boolean isColliding(Vector3f checkedPosition) {
        // No collisions for this entity
        return false;
    }

}

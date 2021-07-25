package engine.world;

import engine.Camera;
import engine.entities.Entity;
import engine.entities.Ground;
import engine.entities.TestEntity;
import engine.graphics.Light;
import org.joml.Vector3f;

public class WorldBuilder {

    public static World testWorld1() {
        World world = new World("test") {
            @Override
            public void tick(double dt) {
                this.getCamera().move();
            }
        };
        Entity ground = new Ground("Ground", new Vector3f(0, 0, 0), new Vector3f(1), new Vector3f(0));
        Camera camera = new Camera();
        world.setCamera(camera);
        world.addEntity(ground);
        world.addLight(new Light("light_0", new Vector3f(0, 200, 0), new Vector3f(0.4f), new Vector3f(1f)));
        return world;
    }

}

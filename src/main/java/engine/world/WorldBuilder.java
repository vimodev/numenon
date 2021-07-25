package engine.world;

import engine.Camera;
import engine.entities.Entity;
import engine.entities.Ground;
import engine.entities.TestEntity;
import engine.graphics.Light;
import org.joml.Vector3f;

public class WorldBuilder {

    public static World testWorld1() {
        World world = new World("test");
        Entity ground = new Ground("Ground", new Vector3f(0, -103, 0), new Vector3f(100), new Vector3f(0));
        Entity entity = new TestEntity("test", new Vector3f(0, 0, -100),new Vector3f(5), new Vector3f(0));
        Entity entity2 = new TestEntity("test2", new Vector3f(-50, 5, -100),new Vector3f(5), new Vector3f(0));
        Camera camera = new Camera();
        world.setCamera(camera);
        world.addEntity(entity);
        world.addEntity(entity2);
        world.addEntity(ground);
        world.addLight(new Light("light_0", new Vector3f(0, 40, 0), new Vector3f(0.1f), new Vector3f(1f)));
        return world;
    }

}

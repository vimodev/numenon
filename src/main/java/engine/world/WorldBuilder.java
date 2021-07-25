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
            double accum = 0;

            @Override
            public void tick(double dt) {
                this.getCamera().move();
                accum += dt;
                if (accum >= 10000) accum -= 10000;
                Light sun = getLightByName("sun");
                sun.setPosition(new Vector3f(
                        500 * (float) Math.sin(accum / 5),
                        200,
                        500 * (float) Math.cos(accum / 5)
                ));
            }
        };
        Entity ground = new Ground("Ground", new Vector3f(0, 0, 0), new Vector3f(1), new Vector3f(0));
        Camera camera = new Camera();
        world.setCamera(camera);
        world.addEntity(ground);
        world.addLight(new Light("sun", new Vector3f(0, 200, 0), new Vector3f(0f), new Vector3f(0.7f)));
        world.addLight(new Light("light_0", new Vector3f(-100, 30, 0), new Vector3f(0f), new Vector3f(1f)));
        return world;
    }

}

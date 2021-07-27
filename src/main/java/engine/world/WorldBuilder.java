package engine.world;

import engine.Camera;
import engine.Terrain;
import engine.entities.Entity;
import engine.entities.Ground;
import engine.entities.TestEntity;
import engine.graphics.Light;
import engine.graphics.Texture;
import org.joml.Vector3f;
import utility.Global;

import static org.lwjgl.glfw.GLFW.*;

public class WorldBuilder {

    public static World testWorld1() {
        World world = new World("test") {
            double accum = 0;

            @Override
            public void tick(double dt) {
                this.getCamera().terrainBoundMove(terrain, dt);
                accum += dt;
                if (accum >= 10000) accum -= 10000;
                Light sun = getLightByName("sun");
                sun.setPosition(new Vector3f(
                        250 * (float) Math.sin(accum / 2),
                        125,
                        250 * (float) Math.cos(accum / 2)
                ));
            }
        };
        Camera camera = new Camera();
        world.setCamera(camera);
        //world.addEntity(ground);
        world.addLight(new Light("sun", new Vector3f(0, 500, 0), new Vector3f(0f), new Vector3f(1f)));
        Terrain terrain = new Terrain(
                "heightmap2.png",
                new Texture("grass.png"),
                1000, 1000, 100,
                new Vector3f(0));
        world.setTerrain(terrain);
        return world;
    }

}

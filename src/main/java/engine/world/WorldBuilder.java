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
                this.getCamera().move();
                if (glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_SPACE) == 1) {
                    System.out.println(getCamera().getPosition());
                }
                accum += dt;
                if (accum >= 10000) accum -= 10000;
                Light sun = getLightByName("sun");
                sun.setPosition(new Vector3f(
                        250 * (float) Math.sin(accum / 2),
                        125,
                        250 * (float) Math.cos(accum / 2)
                ));
                getEntityByName("test").setPosition(sun.getPosition());
                getEntityByName("test").translate(new Vector3f(0, -1, 0));
            }
        };
        Entity test = new TestEntity("test", new Vector3f(0), new Vector3f(20), new Vector3f(0));
        world.addEntity(test);
        Camera camera = new Camera();
        camera.setPosition(new Vector3f(0, 600, 880));
        camera.pitch(35);
        world.setCamera(camera);
        //world.addEntity(ground);
        world.addLight(new Light("sun", new Vector3f(0, 500, 0), new Vector3f(0f), new Vector3f(1f)));
        Terrain terrain = new Terrain(
                "australia.jpg",
                new Texture("grass.png"),
                1000, 1000, 10,
                new Vector3f(0));
        world.setTerrain(terrain);
        return world;
    }

}

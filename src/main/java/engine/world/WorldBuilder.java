package engine.world;

import engine.Camera;
import engine.entities.Boulder;
import engine.entities.Pine;
import engine.entities.Player;
import engine.graphics.Light;
import org.joml.Vector3f;
import utility.Global;
import utility.Timer;

import static org.lwjgl.glfw.GLFW.*;

public class WorldBuilder {

    public static World testWorld1() {
        World world = new World("test") {
            double accum = 0;
            boolean freeCam = false;


            @Override
            public void tick(double dt) {
                if (glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_F9) == 1) {
                    freeCam = true;
                } else if (glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_F8) == 1) {
                    freeCam = false;
                }
                if (freeCam) {
                    this.getCamera().terrainBoundMove(terrain, dt);
                } else {
                    getPlayer().update(dt, this);
                    camera.follow(getPlayer(), terrain,2.5f, 1.75f);
                    camera.lookAt(getPlayer().getPosition().add(0, 1.35f, 0, new Vector3f()));
                    getLightByName("sun").setPosition(new Vector3f(-500f, 500, 0).add(getPlayer().getPosition()));
                }
                accum += dt;
                if (accum >= 10000) accum -= 10000;
            }
        };
        Camera camera = new Camera();
        world.setCamera(camera);
        camera.setPosition(new Vector3f(0, 200, 500));
        camera.pitch(30);
        world.addLight(new Light("sun", new Vector3f(-500f, 500, 0), new Vector3f(0.1f), new Vector3f(0.9f)));
        Terrain terrain =
                new Terrain(
                    250, 250, 25, 215,
                    new Vector3f(0),
                    new TexturePack(
                        "grass_blendmap.png",
                        "grass.png",
                        "grassFlowers.png",
                        "sand.png",
                        "stone.png"),
                        0, 0, -4f
                );
        world.setTerrain(terrain);
        world.setPlayer(
                new Player("player",
                new Vector3f(0, Math.max(terrain.getWaterLevel(), terrain.sample(0, 0)), 0),
                new Vector3f(1f / 9f),
                new Vector3f(0))
        );
        return world;
    }

}

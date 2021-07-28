package engine.world;

import engine.Camera;
import engine.entities.Player;
import engine.graphics.Light;
import org.joml.Vector3f;
import utility.Global;

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
                    getPlayer().update(dt, terrain);
                    camera.follow(getPlayer(), terrain,2, 1.75f);
                    camera.lookAt(getPlayer().getPosition().add(0, 1f, 0, new Vector3f()));
                }
                accum += dt;
                if (accum >= 10000) accum -= 10000;
            }
        };
        Camera camera = new Camera();
        world.setCamera(camera);
        camera.setPosition(new Vector3f(0, 200, 500));
        camera.pitch(30);
        world.addLight(new Light("sun", new Vector3f(-750, 500, 450), new Vector3f(0f), new Vector3f(1f)));
        Terrain terrain =
                new Terrain(
                    "australia.jpg",
                    3000, 3000, 25, 500,
                    new Vector3f(0),
                    new TexturePack(
                        "valley_blendMap.png",
                        "grass.png",
                        "grassFlowers.png",
                        "mud.png",
                        "path.png")
                );
        world.setTerrain(terrain);
        world.setPlayer(new Player("player", new Vector3f(0, 5, 0), new Vector3f(1f / 24f), new Vector3f(0)));
        return world;
    }

}

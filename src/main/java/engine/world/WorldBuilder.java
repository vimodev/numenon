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
                }
                accum += dt;
                if (accum >= 10000) accum -= 10000;
            }
        };
        Camera camera = new Camera();
        world.setCamera(camera);
        camera.setPosition(new Vector3f(0, 200, 500));
        camera.pitch(30);
        world.addLight(new Light("sun", new Vector3f(-500f, 500, 0), new Vector3f(0f), new Vector3f(1f)));
        Terrain terrain =
                new Terrain(
                    "",
                    500, 500, 25, 430,
                    new Vector3f(0),
                    new TexturePack(
                        "grass_blendmap.png",
                        "grass.png",
                        "grassFlowers.png",
                        "mud.png",
                        "path.png")
                );
        world.setTerrain(terrain);
        world.setPlayer(new Player("player", new Vector3f(0, 5, 0), new Vector3f(1f / 24f), new Vector3f(0)));
        for (int i = 0; i < 1000; i++) {
            float x = ((float) Math.random() - 0.5f) * world.getTerrain().getWidth();
            float z = ((float) Math.random() - 0.5f) * world.getTerrain().getHeight();
            world.addEntity(
                    new Pine(
                            "pine" + i,
                            new Vector3f(
                                    x,
                                    world.getTerrain().sample(x, z) - 0.3f,
                                    z
                            ),
                            new Vector3f(0.2f + (float) Math.random() * 0.2f),
                            new Vector3f(0, (float) Math.random() * 360f, 0)
                    )
            );
            x = ((float) Math.random() - 0.5f) * world.getTerrain().getWidth();
            z = ((float) Math.random() - 0.5f) * world.getTerrain().getHeight();
            world.addEntity(
                    new Boulder(
                            "boulder" + i,
                            new Vector3f(
                                    x,
                                    world.getTerrain().sample(x, z),
                                    z
                            ),
                            new Vector3f(0.3f * (float) Math.random() + 0.05f),
                            new Vector3f((float) Math.random() * 360f, (float) Math.random() * 360f, (float) Math.random() * 360f)
                    )
            );
        }
        return world;
    }

}

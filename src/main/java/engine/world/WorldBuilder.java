package engine.world;

import engine.Camera;
import engine.entities.Player;
import engine.graphics.Light;
import org.joml.Vector3f;

public class WorldBuilder {

    public static World testWorld1() {
        World world = new World("test") {
            double accum = 0;

            @Override
            public void tick(double dt) {
                //this.getCamera().terrainBoundMove(terrain, dt);
                getPlayer().update(dt, terrain);
                camera.follow(getPlayer(), terrain,25, 15);
                camera.lookAt(getPlayer().getPosition().add(0, 1.5f, 0, new Vector3f()));
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
                    "valley.png",
                    1000, 1000, 100, 150,
                    new Vector3f(0),
                    new TexturePack(
                        "valley_blendMap.png",
                        "grassy2.png",
                        "grassFlowers.png",
                        "mud.png",
                        "path.png")
                );
        world.setTerrain(terrain);
        world.setPlayer(new Player("player", new Vector3f(0, 5, 0), new Vector3f(0.2f), new Vector3f(0)));
        return world;
    }

}

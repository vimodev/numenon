package engine.world;

import engine.Camera;
import engine.entities.Player;
import engine.graphics.Light;
import engine.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class World {

    protected String name;
    protected List<Entity> entities;
    protected List<Light> lights;
    protected Camera camera;
    protected Terrain terrain;
    protected Player player;

    public World(String name) {
        entities = new ArrayList<>();
        lights = new ArrayList<>();
        this.name = name;
    }

    public World(String name, Terrain terrain, Player player) {
        entities = new ArrayList<>();
        lights = new ArrayList<>();
        this.name = name;
        this.player = player;
        this.terrain = terrain;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void tick(double dt) {}

    public Entity getEntityByName(String name) {
        for (Entity entity : entities) {
            if (entity.getName() == name) {
                return entity;
            }
        }
        return null;
    }

    public Light getLightByName(String name) {
        for (Light light : lights) {
            if (light.getName() == name) {
                return light;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}

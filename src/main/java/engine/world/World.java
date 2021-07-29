package engine.world;

import engine.Camera;
import engine.entities.Player;
import engine.graphics.Light;
import engine.entities.Entity;
import engine.graphics.Renderer;
import utility.Config;

import java.util.ArrayList;
import java.util.List;

public class World {

    protected String name;
    protected List<Entity> entities;
    protected List<Light> lights;
    protected Camera camera;
    protected Player player;

    protected Terrain terrain;
    protected Terrain[] neighbours;

    protected List<Entity> renderedEntities;
    protected List<Entity> collisionCheckedEntities;
    protected Thread neighbourGenerator;

    public World(String name) {
        entities = new ArrayList<>();
        lights = new ArrayList<>();
        renderedEntities = new ArrayList<>();
        collisionCheckedEntities = new ArrayList<>();
        this.neighbours = new Terrain[8];
        this.name = name;
    }

    public World(String name, Terrain terrain, Player player) {
        entities = new ArrayList<>();
        lights = new ArrayList<>();
        renderedEntities = new ArrayList<>();
        collisionCheckedEntities = new ArrayList<>();
        this.name = name;
        this.player = player;
        this.terrain = terrain;
        this.neighbours = new Terrain[8];
        this.neighbourGenerator = new Thread(new GenerateNeighbours(this));
        this.neighbourGenerator.start();
    }

    public void updateLists() {
        // Update rendered entities based on view distance
        renderedEntities.clear();
        collisionCheckedEntities.clear();
        for (Entity entity : entities) {
            float distance = entity.getPosition().distance(camera.getPosition());
            if (distance <= Config.ENTITY_VIEW_DISTANCE) {
                renderedEntities.add(entity);
                // Render distance always >>>> collision distance so check that only when rendered
                if (distance <= Config.ENTITY_COLLISION_CHECK_RADIUS) {
                    collisionCheckedEntities.add(entity);
                }
            }
        }
    }

    public List<Entity> getRenderedEntities() {
        return renderedEntities;
    }

    public List<Entity> getCollisionCheckedEntities() {
        return collisionCheckedEntities;
    }

    public void render() {
        Renderer.render(this);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
        this.neighbours = new Terrain[8];
        this.neighbourGenerator = new Thread(new GenerateNeighbours(this));
        this.neighbourGenerator.start();
    }

    public void setNewCenter(Terrain center) {
        this.terrain = center;
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

    public Terrain[] getNeighbours() {
        return neighbours;
    }
}

package engine;

import engine.entities.Entity;

import java.util.ArrayList;
import java.util.List;

public class World {

    protected String name;
    protected List<Entity> entities;
    protected Camera camera;

    public World(String name) {
        entities = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

package engine.world;

import engine.entities.Entity;

public class EntityQueueItem {

    public Entity target;
    public String obj;
    public String texture;
    public float[] vertices;
    public float[] textureCoords;
    public float[] normals;
    public int[] indices;

    public EntityQueueItem(Entity target, String obj, String texture, float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.target = target;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
        this.obj = obj;
        this.texture = texture;
    }

}

package engine.world;

public class TerrainQueueItem {

    public Terrain target;
    public float[] vertices;
    public float[] textureCoords;
    public float[] normals;
    public int[] indices;

    public TerrainQueueItem(Terrain target, float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.target = target;
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
    }
}

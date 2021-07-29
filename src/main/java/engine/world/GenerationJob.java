package engine.world;

import org.joml.Vector3f;

public class GenerationJob implements Runnable {

    private int offset;
    private int numberOfThreads;

    private float[][] heights;
    private int resolution;

    private float[] vertices;
    private float[] normals;
    private float[] textureCoords;
    private int[] indices;

    private Terrain terrain;

    public GenerationJob(int offset, int numberOfThreads, float[][] heights, int resolution, float[] vertices, float[] normals, int[] indices, float[] textureCoords, Terrain terrain) {
        this.offset = offset;
        this.numberOfThreads = numberOfThreads;
        this.heights = heights;
        this.resolution = resolution;
        this.vertices = vertices;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.terrain = terrain;
        this.indices = indices;
    }

    @Override
    public void run() {
        float width = terrain.getWidth();
        float height = terrain.getHeight();
        for(int i = offset ;i < resolution; i += numberOfThreads){
            int vertexPointer = resolution * i;
            for(int j = 0; j < resolution; j++){
                float jR = (float) j / ((float) resolution - 1);
                float iR = (float) i / ((float) resolution - 1);
                vertices[vertexPointer * 3] = jR * width - width / 2;
                float h = terrain.getRandomHeight(j, i);
                vertices[vertexPointer * 3 + 1] = h;
                heights[j][i] = h;
                vertices[vertexPointer * 3 + 2] = iR * height - height / 2;

                Vector3f normal = terrain.calculateRandomNormal(j, i);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = jR;
                textureCoords[vertexPointer * 2 + 1] = iR;
                vertexPointer++;
            }
        }
    }

}

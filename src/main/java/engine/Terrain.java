package engine;

import engine.graphics.Light;
import engine.graphics.Material;
import engine.graphics.Texture;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TerrainShader;
import engine.world.World;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.CallbackI;
import utility.Config;
import utility.Utility;

import javax.imageio.ImageIO;
import java.awt.image.Raster;
import java.util.List;

public class Terrain {

    private Raster raster;
    private String heightMap;
    private float width;
    private float height;
    private int resolution = 1000;
    private float yScale;
    private Shader shader;
    private Texture texture;
    private Model model;
    private Vector3f position;
    private Material material = new Material(new Vector3f(1), new Vector3f(1));

    public Terrain(String heightMap, Texture texture, float width, float height, float yScale, Vector3f position) {
        this.shader = new TerrainShader();
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.yScale = yScale;
        this.heightMap = heightMap;
        this.position = position;
        try {
            this.raster = ImageIO.read(
                    this.getClass().getResource(Config.HEIGHTMAP_LOCATION + heightMap)
            ).getData();
        } catch (Exception e) {
            System.err.println("Could not read height map.");
            e.printStackTrace();
        }
        generateModelFromRaster();
    }

    public void render(World world) {
        Model model = this.model;
        Shader shader = this.shader;
        Material material = this.material;
        shader.use();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        // Matrices to set
        shader.setUniform("transformationMatrix", Utility.createTransformationMatrix(this.position, new Vector3f(0), new Vector3f(1)));
        shader.setUniform("projectionMatrix", world.getCamera().getProjection());
        shader.setUniform("viewMatrix", world.getCamera().getTransformation());
        List<Light> lights = world.getLights();
        // Set number of lights and light properties
        shader.setUniform("numberOfLights", lights.size());
        for (Light light : lights) {
            shader.setUniform("lightPositions[" + lights.indexOf(light) + "]", light.getPosition());
            shader.setUniform("lightAmbients[" + lights.indexOf(light) + "]", light.getAmbient());
            shader.setUniform("lightDiffuses[" + lights.indexOf(light) + "]", light.getDiffuse());
            shader.setUniform("lightAttenuations[" + lights.indexOf(light) + "]", light.getAttenuation());
        }
        // Set material uniforms
        shader.setUniform("materialAmbient", material.getAmbient());
        shader.setUniform("materialDiffuse", material.getDiffuse());
        // Set camera position for easy access
        shader.setUniform("cameraPosition", world.getCamera().getPosition());
        // Bind texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        // Actually render
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        // Unbind everything
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        shader.unuse();
    }

    public float sample(float x, float z) {
        return 0;
    }

    private float sampleModel(float x, float z) {
        // Find real location
        float rasterX = ((x + this.width / 2) / this.width) * (raster.getWidth() - 1);
        float rasterZ = ((z + this.height / 2) / this.height) * (raster.getHeight() - 1);
        // Get nearest integral locations
        int floorX = (int) Math.floor(rasterX); int ceilX = (int) Math.ceil(rasterX);
        int floorZ = (int) Math.floor(rasterZ); int ceilZ = (int) Math.ceil(rasterZ);
        // Handle exact points
        if (floorX == ceilX) {
            if (ceilX < raster.getWidth() - 1) {
                ceilX++;
            } else {
                floorX--;
            }
        }
        if (floorZ == ceilZ) {
            if (ceilZ < raster.getHeight() - 1) {
                ceilZ++;
            } else {
                floorZ--;
            }
        }
        // Fetch these locations from the heightmap
        float[] buff = new float[5];
        this.raster.getPixel(floorX, floorZ, buff);
        float y1 = (buff[0] / 255f) * yScale;
        this.raster.getPixel(floorX, ceilZ, buff);
        float y2 = (buff[0] / 255f) * yScale;
        this.raster.getPixel(ceilX, floorZ, buff);
        float y3 = (buff[0] / 255f) * yScale;
        this.raster.getPixel(ceilX, ceilZ, buff);
        float y4 = (buff[0] / 255f) * yScale;
        // Bilinear interpolation
        float y13 = ((ceilX - rasterX) / (ceilX - floorX)) * y1 + ((rasterX - floorX) / (ceilX - floorX)) * y3;
        float y24 = ((ceilX - rasterX) / (ceilX - floorX)) * y2 + ((rasterX - floorX) / (ceilX - floorX)) * y4;
        float result = ((ceilZ - rasterZ) / (ceilZ - floorZ)) * y13 + ((rasterZ - floorZ) / (ceilZ - floorZ)) * y24;
        return result;
    }

    private void generateModelFromRaster() {
        int vertexCount = resolution * resolution;
        float vertices[] = new float[vertexCount * 3];
        float normals[] = new float[vertexCount * 3];
        float textureCoords[] = new float[vertexCount * 2];
        int[] indices = new int[6*(resolution-1)*(resolution-1)];
        int vertexPointer = 0;
        for(int i = 0 ;i < resolution; i++){
            for(int j = 0; j < resolution; j++){
                // Positions
                float x = (float)j/((float)resolution - 1) * width - width / 2;
                float z = (float)i/((float)resolution - 1) * height - height / 2;
                vertices[vertexPointer*3] = x;
                vertices[vertexPointer*3+1] = sampleModel(x, z);
                vertices[vertexPointer*3+2] = z;

                // Normals
                float x_prev = (float)Math.max(j - 1, 0)/((float)resolution - 1) * width - width / 2;
                float x_next = (float)Math.min(j + 1, resolution - 1)/((float)resolution - 1) * width - width / 2;
                float z_prev = (float)Math.max(i - 1, 0)/((float)resolution - 1) * height - height / 2;
                float z_next = (float)Math.min(i + 1, resolution - 1)/((float)resolution - 1) * height - height / 2;

                // The points to consider
                Vector3f current = new Vector3f(x, sampleModel(x,z), z);
                Vector3f prev_x = new Vector3f(x_prev, sampleModel(x_prev, z), z);
                Vector3f next_x = new Vector3f(x_next, sampleModel(x_next, z), z);
                Vector3f prev_z = new Vector3f(x, sampleModel(x, z_prev), z_prev);
                Vector3f next_z = new Vector3f(x, sampleModel(x, z_next), z_next);
                // Vectors from current to the other points, grid aligned
                Vector3f xn = new Vector3f(); Vector3f xp = new Vector3f();
                Vector3f zn = new Vector3f(); Vector3f zp = new Vector3f();
                prev_x.sub(current, xp); next_x.sub(current, xn);
                prev_z.sub(current, zp); next_z.sub(current, zn);
                // Get average vector for both dimensions
                Vector3f avg_x = new Vector3f(); Vector3f avg_z = new Vector3f();
                float angle_x = 0; float angle_z = 0;
                angle_x = xn.angle(xp); angle_z = zn.angle(zp);
                xn.rotateZ(angle_x / 2f, avg_x); avg_x.normalize();
                zn.rotateX(-angle_z / 2f, avg_z); avg_z.normalize();
                Vector3f normal = new Vector3f();
                avg_x.add(avg_z, normal); normal.div(2f);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;

                // Texture coords
                textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
                vertexPointer++;
            }
        }
        // Indices
        int pointer = 0;
        for(int gz=0;gz<resolution-1;gz++){
            for(int gx=0;gx<resolution-1;gx++){
                int topLeft = (gz*resolution)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*resolution)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        this.model = Loader.loadToVAO(vertices, textureCoords, normals, indices);
        this.model.setTexture(texture);
    }

}

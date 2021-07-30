package engine.world;

import engine.Loader;
import engine.entities.Boulder;
import engine.entities.Entity;
import engine.entities.Fern;
import engine.entities.Pine;
import engine.graphics.Light;
import engine.graphics.Material;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TerrainShader;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utility.Config;
import utility.Global;
import utility.Timer;
import utility.Utility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.List;

/**
 * Terrain for player to walk on in a world
 */
public class Terrain {

    private List<Entity> terrainEntities;
    private static final int NUM_TREES = 250;
    private static final int NUM_ROCKS = 250;
    private static final int NUM_FERNS = 25;

    private Raster raster;
    private BufferedImage image;
    private float[][] heights;

    private String heightMap;
    private boolean randomlyGenerated = false;

    public int x_offset = 0;
    public int z_offset = 0;

    private float width;
    private float height;
    private int resolution;
    private float yScale;

    private float waterLevel;

    private static Shader shader = new TerrainShader();
    private Model model;
    public boolean readyToRender = true;

    private Vector3f position;

    private Material material = new Material(new Vector3f(1), new Vector3f(1));

    private TexturePack texturePack;

    public Terrain(String heightMap, float width, float height, float yScale, int resolution, Vector3f position, TexturePack texturePack,
                   int x_offset, int z_offset, float waterLevel) {
        this.waterLevel = waterLevel;
        this.texturePack = texturePack;
        this.width = width;
        this.height = height;
        this.yScale = yScale;
        this.heightMap = heightMap;
        this.position = position;
        this.resolution = resolution;
        this.x_offset = x_offset;
        this.z_offset = z_offset;
        this.terrainEntities = new ArrayList<>();
        // If no height map specified, we randomly generate
        if (heightMap == "" || heightMap == null) {
            randomlyGenerated = true;
            generateModelFromRandom();
            populate();
            return;
        }
        // Read the height map
        try {
            BufferedImage i = ImageIO.read(
                    this.getClass().getResource(Config.HEIGHTMAP_LOCATION + heightMap)
            );
            this.image = i;
            this.raster = i.getData();
        } catch (Exception e) {
            System.err.println("Could not read height map.");
            e.printStackTrace();
        }
        // Generate the model from the heightmap
        generateModelFromRaster();
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void populate() {
        terrainEntities.clear();
        for (int i = 0; i < NUM_TREES; i++) {
            float x = ((float) Math.random() - 0.5f) * getWidth() + position.x;
            float z = ((float) Math.random() - 0.5f) * getHeight() + position.z;
            float y = sample(x, z);
            if (y < waterLevel) {
                i--; continue;
            }
            addTerrainEntity(
                    new Pine("pine_" + x_offset + "_" + x_offset + "_" + i,
                        new Vector3f(x, y - 0.3f, z),
                        new Vector3f(0.2f + (float) Math.random() * 0.2f),
                        new Vector3f(0, (float) Math.random() * 360f, 0)
                    )
            );
        }
        for (int i = 0; i < NUM_ROCKS; i++) {
            float x = ((float) Math.random() - 0.5f) * getWidth() + position.x;
            float z = ((float) Math.random() - 0.5f) * getHeight() + position.z;
            addTerrainEntity(
                    new Boulder("rock_" + x_offset + "_" + x_offset + "_" + i,
                            new Vector3f(x, sample(x, z) - 0.3f, z),
                            new Vector3f(0.3f * (float) Math.random() + 0.05f),
                            new Vector3f((float) Math.random() * 360f, (float) Math.random() * 360f, (float) Math.random() * 360f)
                    )
            );
        }
        for (int i = 0; i < NUM_FERNS; i++) {
            float x = ((float) Math.random() - 0.5f) * getWidth() + position.x;
            float z = ((float) Math.random() - 0.5f) * getHeight() + position.z;
            float y = sample(x, z);
            if (y < waterLevel) {
                i--; continue;
            }
            addTerrainEntity(
                    new Fern("fern_" + x_offset + "_" + x_offset + "_" + i,
                            new Vector3f(x, y - 0.3f, z),
                            new Vector3f(0.1f * (float) Math.random() + 0.02f),
                            new Vector3f(0, (float) Math.random() * 360f, 0)
                    )
            );
        }
    }

    public List<Entity> getTerrainEntities() {
        return terrainEntities;
    }

    public void addTerrainEntity(Entity entity) {
        terrainEntities.add(entity);
    }

    public static void destroy(Terrain terrain) {
        if (terrain == null) return;
        terrain.getModel().destroy();
    }

    public void setTexturePack(TexturePack texturePack) {
        this.texturePack = texturePack;
    }

    public Vector2i getOffsets() {
        return new Vector2i(x_offset, z_offset);
    }

    /**
     * Render this terrain with camera and lights from given world
     * @param world
     */
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
        // Bind textures for blend mapping the terrain
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
        shader.setUniform("backgroundTexture", 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
        shader.setUniform("rTexture", 1);
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
        shader.setUniform("gTexture", 2);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
        shader.setUniform("bTexture", 3);
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBlendMap().getTextureID());
        shader.setUniform("blendMap", 4);
        // Water level
        shader.setUniform("waterLevel", waterLevel);
        // Actually render
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        // Unbind everything
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        shader.unuse();
    }

    /**
     * Given world coordinates x and z, get the terrain height
     * defaults to 0 if out of bounds
     * @param x
     * @param z
     * @return
     */
    public float sample(float x, float z) {
        if (Math.abs(x - position.x) > Math.abs(width / 2) || Math.abs(z - position.z) > Math.abs(height / 2)) {
            return 0;
        }
        x -= position.x; z -= position.z;
        x += width / 2; z += height / 2;
        float squareSizeX = width / (resolution - 1);
        float squareSizeZ = height / (resolution - 1);
        int gridX = (int) Math.floor(x / squareSizeX);
        int gridZ = (int) Math.floor(z / squareSizeZ);
        float xC = (x % squareSizeX) / squareSizeX;
        float zC = (z % squareSizeZ) / squareSizeZ;
        if (xC <= 1 - zC) {
            return Utility.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ], 0), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xC, zC));
        } else {
            return Utility.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xC, zC));
        }
    }

    /**
     * Sample the model-space y-coordinate given model-space x and z from the heightmap.
     * Primarily used for constructing the model
     * @param x
     * @param z
     * @return
     */
    private float sampleModelFromHeightmap(float x, float z) {
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
        float value = 0;
        value = (image.getRGB(floorX, floorZ) >> 16) & 0xff;
        float y1 = (value / 255f) * yScale;
        value = (image.getRGB(floorX, ceilZ) >> 16) & 0xff;
        float y2 = (value / 255f) * yScale;
        value = (image.getRGB(ceilX, floorZ) >> 16) & 0xff;
        float y3 = (value / 255f) * yScale;
        value = (image.getRGB(ceilX, ceilZ) >> 16) & 0xff;
        float y4 = (value / 255f) * yScale;
        // Bilinear interpolation
        float y13 = ((ceilX - rasterX) / (ceilX - floorX)) * y1 + ((rasterX - floorX) / (ceilX - floorX)) * y3;
        float y24 = ((ceilX - rasterX) / (ceilX - floorX)) * y2 + ((rasterX - floorX) / (ceilX - floorX)) * y4;
        float result = ((ceilZ - rasterZ) / (ceilZ - floorZ)) * y13 + ((rasterZ - floorZ) / (ceilZ - floorZ)) * y24;
        return result;
    }

    private Vector2f modelLocationFromGrid(int gridX, int gridZ) {
        float x = (float)gridX/((float)resolution - 1) * width - width / 2;
        float z = (float)gridZ/((float)resolution - 1) * height - height / 2;
        return new Vector2f(x, z);
    }

    /**
     * With the height map loaded,
     * construct the model by sampling based on parameters
     */
    private void generateModelFromRaster() {
        heights = new float[resolution][resolution];
        int vertexCount = resolution * resolution;
        float vertices[] = new float[vertexCount * 3];
        float normals[] = new float[vertexCount * 3];
        float textureCoords[] = new float[vertexCount * 2];
        int[] indices = new int[6*(resolution-1)*(resolution-1)];
        int vertexPointer = 0;
        for(int i = 0 ;i < resolution; i++){
            for(int j = 0; j < resolution; j++){
                // Positions
                Vector2f loc = modelLocationFromGrid(j, i);
                float x = loc.x; float z = loc.y;
                float sampleHeight = sampleModelFromHeightmap(x, z);
                heights[j][i] = sampleHeight;
                vertices[vertexPointer*3] = x;
                vertices[vertexPointer*3+1] = sampleHeight;
                vertices[vertexPointer*3+2] = z;

                // Normals
                float x_prev = (float)Math.max(j - 1, 0)/((float)resolution - 1) * width - width / 2;
                float x_next = (float)Math.min(j + 1, resolution - 1)/((float)resolution - 1) * width - width / 2;
                float z_prev = (float)Math.max(i - 1, 0)/((float)resolution - 1) * height - height / 2;
                float z_next = (float)Math.min(i + 1, resolution - 1)/((float)resolution - 1) * height - height / 2;
                // The points to consider
                Vector3f current = new Vector3f(x, sampleModelFromHeightmap(x,z), z);
                Vector3f prev_x = new Vector3f(x_prev, sampleModelFromHeightmap(x_prev, z), z);
                Vector3f next_x = new Vector3f(x_next, sampleModelFromHeightmap(x_next, z), z);
                Vector3f prev_z = new Vector3f(x, sampleModelFromHeightmap(x, z_prev), z_prev);
                Vector3f next_z = new Vector3f(x, sampleModelFromHeightmap(x, z_next), z_next);
                // Vectors from current to the other points, grid aligned
                Vector3f xn = new Vector3f(); Vector3f xp = new Vector3f();
                Vector3f zn = new Vector3f(); Vector3f zp = new Vector3f();
                prev_x.sub(current, xp); next_x.sub(current, xn);
                prev_z.sub(current, zp); next_z.sub(current, zn);
                // Get average vector for both dimensions
                Vector3f avg_x = new Vector3f(); Vector3f avg_z = new Vector3f();
                float angle_x = 0; float angle_z = 0;
                angle_x = xn.angle(xp); angle_z = zn.angle(zp);
                if (next_x.y <= current.y || prev_x.y <= current.y) angle_x = (float) Math.PI * 2 - angle_x;
                if (next_z.y <= current.y || prev_z.y <= current.y) angle_z = (float) Math.PI * 2 - angle_z;
                xn.rotateZ(angle_x / 2f, avg_x); avg_x.normalize();
                zn.rotateX(-angle_z / 2f, avg_z); avg_z.normalize();

                Vector3f normal = new Vector3f();
                avg_x.add(avg_z, normal); normal.div(2f);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;

                // Texture coords
                textureCoords[vertexPointer*2] = (float)j/((float)resolution - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)resolution - 1);
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
        if (Thread.currentThread().getName() != "main") {
            readyToRender = false;
            return;
        }
        this.model = Loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    /**
     * Generate the terrain model from the HeightsGenerator and offset
     */
    private synchronized void generateModelFromRandom() {
        // We initialize the locations for the calculated vertex properties
        heights = new float[resolution][resolution];
        int vertexCount = resolution * resolution;
        float vertices[] = new float[vertexCount * 3];
        float normals[] = new float[vertexCount * 3];
        float textureCoords[] = new float[vertexCount * 2];
        int[] indices = new int[6*(resolution-1)*(resolution-1)];

        // Vertex calculation
        // And we dispatch threads to perform the calculations
        int NUMBER_OF_THREADS = 2;
        List<Thread> genJobs = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            genJobs.add(new Thread(new GenerationJob(i, NUMBER_OF_THREADS, heights, resolution, vertices, normals, indices, textureCoords, this)));
            genJobs.get(i).run();
        }
        // Indices filling
        // And we dispatch threads for the indices filling
        int INDICES_THREADS = 2;
        List<Thread> indexJobs = new ArrayList<>();
        for (int i = 0; i < INDICES_THREADS; i++) {
            indexJobs.add(new Thread(new IndicesJob(resolution, indices, i, INDICES_THREADS)));
            indexJobs.get(i).run();
        }
        // And we wait for all the threads to finish
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            try {
                genJobs.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < INDICES_THREADS; i++) {
            try {
                indexJobs.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Now we are done calculating, we need to load model to OpenGL
        // Only main thread can do this so we check if this is main thread
        if (Thread.currentThread().getName().equals("main")) {
            this.model = Loader.loadToVAO(vertices, textureCoords, normals, indices);
        } else {
            // If not the main thread, we add the data to a Loader queue so that main thread can load it later
            Global.terrain_queue_mutex.lock();
            Loader.addToTerrainQueue(new TerrainQueueItem(this, vertices, textureCoords, normals, indices));
            readyToRender = false;
            Global.terrain_queue_mutex.unlock();
        }
    }

    /**
     * Given grid x and grid z, get the generated / cached height
     * @param x
     * @param z
     * @return
     */
    public float getRandomHeight(int x, int z) {
        if (x-1 < 0 || z-1 < 0 || x+1 >= resolution || z+1 >= resolution) {
            return HeightsGenerator.generateHeight(x + x_offset * (resolution - 1), z + z_offset * (resolution - 1));
        }
        if (heights[x][z] != 0) {
            return heights[x][z];
        }
        float h = HeightsGenerator.generateHeight(x + x_offset * (resolution - 1), z + z_offset * (resolution - 1));
        heights[x][z] = h;
        return h;
    }

    /**
     * Given grid x and grid z, get the normal vector
     * @param x
     * @param z
     * @return
     */
    public Vector3f calculateRandomNormal(int x, int z){
        // Check if heights are already stored, if not, sample them
        float heightL = getRandomHeight(x-1, z);
        float heightR = getRandomHeight(x+1, z);
        float heightD = getRandomHeight(x, z-1);
        float heightU = getRandomHeight(x, z+1);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
        normal.normalize();
        return normal;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getyScale() {
        return yScale;
    }

    public int getResolution() {
        return resolution;
    }

    public Shader getShader() {
        return shader;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Material getMaterial() {
        return material;
    }

    public TexturePack getTexturePack() {
        return texturePack;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}

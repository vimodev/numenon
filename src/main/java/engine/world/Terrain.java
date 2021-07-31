package engine.world;

import engine.Loader;
import engine.entities.*;
import engine.graphics.Light;
import engine.graphics.Material;
import engine.graphics.Renderer;
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
import utility.Global;
import utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Terrain for player to walk on in a world
 */
public class Terrain {

    private List<Entity> terrainEntities;
    private static final int NUM_TREES = 250;
    private static final int NUM_ROCKS = 125;
    private static final int NUM_FERNS = 75;
    private static final int NUM_DAFFODILS = 75;
    private static final int NUM_CROCUS = 75;

    private float[][] heights;

    public int x_offset = 0;
    public int z_offset = 0;

    private float width;
    private float height;
    private int resolution;
    private float yScale;

    private float waterLevel;

    private static Shader shader = new TerrainShader();
    private Model model;
    public boolean readyToRender = false;

    private Vector3f position;

    private Material material = new Material(new Vector3f(1), new Vector3f(1));

    private TexturePack texturePack;

    public Terrain(float width, float height, float yScale, int resolution, Vector3f position, TexturePack texturePack,
                   int x_offset, int z_offset, float waterLevel) {
        this.waterLevel = waterLevel;
        this.texturePack = texturePack;
        this.width = width;
        this.height = height;
        this.yScale = yScale;
        this.position = position;
        this.resolution = resolution;
        this.x_offset = x_offset;
        this.z_offset = z_offset;
        this.terrainEntities = new ArrayList<>();
        generateModelFromRandom();
        populate();
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
            if (y < waterLevel || getRoughNormal(x, z).y <= 0.5) {
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
        for (int i = 0; i < NUM_DAFFODILS; i++) {
            float x = ((float) Math.random() - 0.5f) * getWidth() + position.x;
            float z = ((float) Math.random() - 0.5f) * getHeight() + position.z;
            float y = sample(x, z);
            if (y < waterLevel) {
                i--; continue;
            }
            addTerrainEntity(
                    new Daffodil("daffodil_" + x_offset + "_" + x_offset + "_" + i,
                            new Vector3f(x, y - 0.3f, z),
                            new Vector3f(2f + 1 * (float) Math.random()),
                            new Vector3f(0, (float) Math.random() * 360f, 0)
                    )
            );
        }
        for (int i = 0; i < NUM_CROCUS; i++) {
            float x = ((float) Math.random() - 0.5f) * getWidth() + position.x;
            float z = ((float) Math.random() - 0.5f) * getHeight() + position.z;
            float y = sample(x, z);
            if (y < waterLevel) {
                i--; continue;
            }
            addTerrainEntity(
                    new Crocus("crocus_" + x_offset + "_" + x_offset + "_" + i,
                            new Vector3f(x, y - 0.3f, z),
                            new Vector3f(0.75f + 1 * (float) Math.random()),
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
        shader.setUniform("skyColour", Renderer.skyColour);
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
     * @param x
     * @param z
     * @return
     */
    public float sample(float x, float z) {
        x -= position.x; z -= position.z;
        x += width / 2; z += height / 2;
        float squareSizeX = width / (resolution - 1);
        float squareSizeZ = height / (resolution - 1);
        int gridX = (int) Math.floor(x / squareSizeX);
        int gridZ = (int) Math.floor(z / squareSizeZ);
        float xC = (x % squareSizeX) / squareSizeX;
        float zC = (z % squareSizeZ) / squareSizeZ;
        if (xC <= 1 - zC) {
            // If out of bounds we try to sample any way
            if (Math.abs(x - position.x) >= Math.abs((width / 2) - 2f) || Math.abs(z - position.z) >= Math.abs((height / 2) - 2f)) {
                return Utility.barryCentric(
                        new Vector3f(0, getRandomHeight(gridX, gridZ), 0),
                        new Vector3f(1, getRandomHeight(gridX + 1, gridZ), 0),
                        new Vector3f(0, getRandomHeight(gridX, gridZ + 1), 1),
                        new Vector2f(xC, zC));
            }
            return Utility.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ], 0), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xC, zC));
        } else {
            // If out of bounds we try to sample any way
            if (Math.abs(x - position.x) >= Math.abs((width / 2) - 2f) || Math.abs(z - position.z) >= Math.abs((height / 2) - 2f)) {
                return Utility.barryCentric(
                        new Vector3f(1, getRandomHeight(gridX + 1, gridZ), 0),
                        new Vector3f(1, getRandomHeight(gridX + 1, gridZ + 1), 1),
                        new Vector3f(0, getRandomHeight(gridX, gridZ + 1), 1),
                        new Vector2f(xC, zC));
            }
            return Utility.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                    heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                    heights[gridX][gridZ + 1], 1), new Vector2f(xC, zC));
        }
    }

    private Vector2f modelLocationFromGrid(int gridX, int gridZ) {
        float x = (float)gridX/((float)resolution - 1) * width - width / 2;
        float z = (float)gridZ/((float)resolution - 1) * height - height / 2;
        return new Vector2f(x, z);
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
            readyToRender = true;
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

    public Vector3f getRoughNormal(float x, float z) {
        return calculateRandomNormal(Math.round(x), Math.round(z));
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

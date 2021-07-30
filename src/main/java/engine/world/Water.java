package engine.world;

import engine.Loader;
import engine.graphics.Light;
import engine.graphics.Material;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TerrainShader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utility.Utility;

import java.util.List;

public class Water {

    private float level;
    private float width;
    private float height;
    private int resolution;
    private Vector3f position;
    private Model model;
    private Shader shader;
    private Material material;

    public Water(float level, float width, float height, int resolution) {
        this.level = level;
        this.width = width;
        this.height = height;
        this.resolution = resolution;
        this.shader = new TerrainShader();
        this.material = new Material(new Vector3f(1), new Vector3f(1));
        this.position = new Vector3f(0);
        generateModel();
    }

    public void updatePosition(World world) {
        this.position = world.getTerrain().getPosition();
    }

    public float getLevel() {
        return level;
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
        shader.setUniform("transformationMatrix", Utility.createTransformationMatrix(
                this.position.add(0, level, 0, new Vector3f()),
                new Vector3f(0),
                new Vector3f(1))
        );
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

    private void generateModel() {
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
                vertices[vertexPointer*3] = x;
                vertices[vertexPointer*3+1] = 0;
                vertices[vertexPointer*3+2] = z;
                // Normals
                normals[vertexPointer*3] = 0;
                normals[vertexPointer*3+1] = 1;
                normals[vertexPointer*3+2] = 0;
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
        this.model = Loader.loadToVAO(vertices, textureCoords, normals, indices);
        model.setTexture(Loader.loadTexture("water.png"));
    }

    private Vector2f modelLocationFromGrid(int gridX, int gridZ) {
        float x = (float)gridX/((float)resolution - 1) * width - width / 2;
        float z = (float)gridZ/((float)resolution - 1) * height - height / 2;
        return new Vector2f(x, z);
    }

}

package engine.graphics;

import engine.Camera;
import engine.world.World;
import engine.entities.Entity;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Responsible for rendering models and worlds
 */
public class Renderer {

    public static void pre() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LEQUAL);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void render(World world) {
        for (Entity entity : world.getEntities()) {
            render(entity, world.getCamera(), world.getLights());
        }
    }

    public static void render(Entity entity, Camera camera, List<Light> lights) {
        Model model = entity.getModel();
        Shader shader = entity.getShader();
        Material material = entity.getMaterial();
        shader.use();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        // Matrices to set
        shader.setUniform("transformationMatrix", entity.getMatrix());
        shader.setUniform("projectionMatrix", camera.getProjection());
        shader.setUniform("viewMatrix", camera.getTransformation());
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
        shader.setUniform("cameraPosition", camera.getPosition());
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

    public static void post() {

    }

}

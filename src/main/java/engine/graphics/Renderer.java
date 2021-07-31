package engine.graphics;

import engine.Camera;
import engine.graphics.models.AnimatedModel;
import engine.world.Terrain;
import engine.world.World;
import engine.entities.Entity;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utility.Config;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * Responsible for rendering models and worlds
 */
public class Renderer {

    public static Vector3f skyColour = new Vector3f(0, 0.6f, 1f);

    /**
     * Stuff that has to happen at start of every frame
     */
    public static void pre() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LEQUAL);
        glEnable(GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        glCullFace(GL_BACK);
        GL11.glClearColor(skyColour.x, skyColour.y, skyColour.z, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Render the given world, terrain + player + entities
     * @param world
     */
    public static void render(World world) {
        Renderer.pre();
        world.updateLists();
        // Only if terrain is ready to render do we do so
        if (world.getTerrain().readyToRender) world.getTerrain().render(world);
        // Also render neighbouring terrains
        Terrain[] neighbours = world.getNeighbours();
        for (int i = 0; i < 8; i++) {
            // Only if its present and ready to render
            if (neighbours[i] != null && neighbours[i].readyToRender) {
                neighbours[i].render(world);
            }
        }
        // Also render water
        world.getWater().render(world);
        // Render player
        if (world.getPlayer() != null) {
            render(world.getPlayer(), world.getCamera(), world.getLights());
        }
        // Render entities in range
        Map<Model, List<Entity>> map = world.getRenderedEntitiesMap();
        for (Model model : map.keySet()) {
            renderSameModelEntities(model, map.get(model), world.getCamera(), world.getLights());
        }
    }

    /**
     * Given a list of entities with the given model, render them all efficiently
     * @param model
     * @param entities
     * @param camera
     * @param lights
     */
    public static void renderSameModelEntities(Model model, List<Entity> entities, Camera camera, List<Light> lights) {
        if (entities.get(0).hasTransparency) glDisable(GL_CULL_FACE);
        boolean isAnimated = model instanceof AnimatedModel;
        Shader shader = isAnimated ? Entity.animatedShader : Entity.shader;
        shader.use();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (isAnimated) {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        // THESE UNIFORMS DONT CHANGE DEPENDING ON ENTITY
        shader.setUniform("skyColour", skyColour);
        // Matrices to set
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
        // Set camera position for easy access
        shader.setUniform("cameraPosition", camera.getPosition());
        // If animated, add that too
        if (isAnimated) {
            Matrix4f[] transforms = ((AnimatedModel) model).getJointTransforms();
            for (int i = 0; i < transforms.length; i++) {
                Matrix4f mat = transforms[i];
                shader.setUniform("jointTransforms[" + i + "]", mat);
            }
        }
        // Bind texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());

        // Actually render all the entities
        for (Entity entity : entities) {
            Material material = entity.getMaterial();
            // THESE UNIFORMS CAN CHANGE DEPENDING ON ENTITY
            shader.setUniform("transformationMatrix", entity.getMatrix());
            shader.setUniform("materialAmbient", material.getAmbient());
            shader.setUniform("materialDiffuse", material.getDiffuse());
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        }

        // Unbind everything
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        if (isAnimated) {
            GL20.glDisableVertexAttribArray(3);
            GL20.glDisableVertexAttribArray(4);
        }
        GL30.glBindVertexArray(0);
        shader.unuse();
        glEnable(GL_CULL_FACE);
    }

    /**
     * Render the given entity with the camera and lights
     * @param entity
     * @param camera
     * @param lights
     */
    public static void render(Entity entity, Camera camera, List<Light> lights) {
        if (entity.hasTransparency) glDisable(GL_CULL_FACE);
        Model model = entity.getModel();
        boolean isAnimated = model instanceof AnimatedModel;
        Shader shader = isAnimated ? Entity.animatedShader : Entity.shader;
        Material material = entity.getMaterial();
        shader.use();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        if (isAnimated) {
            GL20.glEnableVertexAttribArray(3);
            GL20.glEnableVertexAttribArray(4);
        }
        shader.setUniform("skyColour", skyColour);
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
        // If animated, add that too
        if (isAnimated) {
            Matrix4f[] transforms = ((AnimatedModel) model).getJointTransforms();
            for (int i = 0; i < transforms.length; i++) {
                Matrix4f mat = transforms[i];
                shader.setUniform("jointTransforms[" + i + "]", mat);
            }
        }
        // Bind texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        // Actually render
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        // Unbind everything
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        if (isAnimated) {
            GL20.glDisableVertexAttribArray(3);
            GL20.glDisableVertexAttribArray(4);
        }
        GL30.glBindVertexArray(0);
        shader.unuse();
        if (entity.hasTransparency) glEnable(GL_CULL_FACE);
    }

    public static void post() {

    }

}

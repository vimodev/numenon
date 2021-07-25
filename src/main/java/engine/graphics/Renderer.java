package engine.graphics;

import engine.Camera;
import engine.entities.Entity;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

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
        GL11.glClearColor(1, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void render(Entity entity, Camera camera) {
        Model model = entity.getModel();
        Shader shader = entity.getShader();
        shader.use();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        shader.setUniform("transformationMatrix", entity.getMatrix());
        shader.setUniform("projectionMatrix", camera.getProjection());
        shader.setUniform("viewMatrix", camera.getTransformation());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.unuse();
    }

    public static void post() {

    }

}

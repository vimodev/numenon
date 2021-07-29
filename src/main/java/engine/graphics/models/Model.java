package engine.graphics.models;

import engine.Loader;
import engine.graphics.Texture;
import org.lwjgl.opengl.GL30;
import utility.Config;

import java.util.List;

/**
 * Represents a 3D model in the engine
 */
public class Model {

    private int vaoID;
    private List<Integer> vbos;
    private int vertexCount;
    private Texture texture;

    public Model(int vaoID, int vertexCount, Texture texture, List<Integer> vbos) {
        this.vbos = vbos;
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.texture = texture;
    }

    public Model(int vaoID, int vertexCount, List<Integer> vbos) {
        this.vbos = vbos;
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.texture = Loader.loadTexture(Config.DEFAULT_TEXTURE);
    }

    public void destroy() {
        GL30.glDeleteVertexArrays(vaoID);
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
    }

    public int getVaoID() {
        return vaoID;
    }

    public List<Integer> getVbos() {
        return vbos;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}

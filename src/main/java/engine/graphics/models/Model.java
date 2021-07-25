package engine.graphics.models;

import engine.Loader;
import engine.graphics.Texture;
import utility.Config;

/**
 * Represents a 3D model in the engine
 */
public class Model {

    private int vaoID;
    private int vertexCount;
    private Texture texture;

    public Model(int vaoID, int vertexCount, Texture texture) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.texture = texture;
    }

    public Model(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
        this.texture = Loader.loadTexture(Config.DEFAULT_TEXTURE);
    }

    public int getVaoID() {
        return vaoID;
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

package engine.gui;

import engine.graphics.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import utility.Utility;

public class GUIElement {

    private Texture texture;
    private Vector2f position;
    private Vector2f scale;

    public GUIElement(String texture, Vector2f position, Vector2f scale) {
        this.texture = new Texture(texture);
        this.position = position;
        this.scale = scale;
    }

    public Matrix4f getMatrix() { return Utility.createTransformationMatrix(position, scale); }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }

}

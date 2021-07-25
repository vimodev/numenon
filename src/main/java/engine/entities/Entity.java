package engine.entities;

import engine.graphics.Material;
import engine.graphics.models.Model;
import engine.graphics.shaders.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static utility.Utility.createTransformationMatrix;

public abstract class Entity {

    protected String name;
    protected Model model;
    protected Vector3f position;
    protected Vector3f scale;
    protected Vector3f rotation;
    protected Shader shader;
    protected Material material;

    public Entity(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        this.name = name;
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    /**
     * Gets the transformation matrix of this entity
     * @return transformation matrix
     */
    public Matrix4f getMatrix() {
        return createTransformationMatrix(position, rotation, scale);
    }

    public Model getModel() {
        return model;
    }

    public Shader getShader() {
        return shader;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void translate(Vector3f t) {
        position.add(t);
    }

    public void rotate(Vector3f r) {
        rotation.add(r);
    }

    public void scale(Vector3f s) {
        scale.mul(s);
    }

    public void addScale(Vector3f s) {
        scale.add(s);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
}

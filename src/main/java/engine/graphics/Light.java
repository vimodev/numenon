package engine.graphics;

import org.joml.Vector3f;

public class Light {

    protected String name;
    protected Vector3f position;
    protected Vector3f ambient;
    protected Vector3f diffuse;

    public Light(String name, Vector3f position, Vector3f ambient, Vector3f diffuse) {
        this.name = name;
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }
}

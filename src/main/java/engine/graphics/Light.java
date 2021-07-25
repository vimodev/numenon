package engine.graphics;

import org.joml.Vector3f;

public class Light {

    protected String name;
    protected Vector3f position;
    protected Vector3f ambient;
    protected Vector3f diffuse;
    protected Vector3f attenuation;
    // Custom attenuation formula: Intensity = e^-(distance * x - y)^z defaults xyz=101 or xyz=001 for no attenuation

    public Light(String name, Vector3f position, Vector3f ambient, Vector3f diffuse) {
        this.name = name;
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.attenuation = new Vector3f(0, 0, 1);
    }

    public Light(String name, Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f attenuation) {
        this.name = name;
        this.position = position;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.attenuation = attenuation;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
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

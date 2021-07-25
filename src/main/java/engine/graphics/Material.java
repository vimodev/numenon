package engine.graphics;

import org.joml.Vector3f;

public class Material {

    // Lighting properties for different kinds of light
    private Vector3f ambient;
    private Vector3f diffuse;

    /**
     * Create a material
     * @param ambient how it reflects ambient light
     * @param diffuse how it reflects diffuse light
     */
    public Material(Vector3f ambient, Vector3f diffuse) {
        this.ambient = ambient;
        this.diffuse = diffuse;
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

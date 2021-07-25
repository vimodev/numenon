package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import utility.Config;
import utility.Global;

import static org.lwjgl.glfw.GLFW.*;

/**
 *  CAMERA CLASS
 *  Contains information about a camera's positioning and
 *  its projection information.
 *  @Author Vincent Moonen
 */
public class Camera {

    // Position of the camera
    private Vector3f position;
    // Projection properties of the camera
    private Matrix4f projection;
    // Orientation properties
    private float pitch = 0;
    private float yaw = 0;
    private float roll = 0;

    /**
     * Create a new camera
     */
    public Camera() {
        position = new Vector3f(0);
        projection = new Matrix4f();
        setProjection((float) Config.VIEW_WIDTH / (float) Config.VIEW_HEIGHT, 70, 0.1f, 10000f);
    }

    public void move() {
        float mv_scl = glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_W) - glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_S);
        float rt_scl = glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_D) - glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_A);
        yaw(rt_scl * Config.CAMERA_TURN_SPEED);
        translate(getDirection().mul(mv_scl * Config.CAMERA_MOVE_SPEED));
    }

    /**
     * Get the transformation matrix for this camera
     * Which is inverse to what you normally do
     * @return Transformation matrix for all other object
     */
    public Matrix4f getTransformation() {
        // Set matrix to id
        Matrix4f M = new Matrix4f();
        M.identity();
        // Apply transformations
        M.rotateX((float) Math.toRadians(pitch));
        M.rotateY((float) Math.toRadians(yaw));
        M.rotateZ((float) Math.toRadians(roll));
        // And inverse of translation
        M.translate(position.mul(-1, new Vector3f()));
        // *note:
        // When we move the camera, we actually move the entire world in the opposite direction, thats why
        return M;
    }

    /**
     * What is the normalized direction vector of this camera
     * @return
     */
    public Vector3f getDirection() {
        // Get the transformation
        Vector4f v = new Vector4f(0, 0, -1, 1);
        Matrix4f M = getTransformation();
        M.translate(position);
        // Apply to vector
        v.mul(M);
        // Extract direction
        return (new Vector3f(-v.x, v.y, v.z)).normalize();
    }

    /**
     * Set projective properties of the camera
     * @param a aspect ratio
     * @param fov field of view, vertically
     * @param znear near plane
     * @param zfar far plane
     */
    public void setProjection(float a, float fov, float znear, float zfar) {
        this.projection.setPerspective(fov, a, znear, zfar);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void translate(Vector3f t) {
        position.add(t);
    }

    public void pitch(float p) {
        pitch += p;
    }

    public void yaw(float y) {
        yaw += y;
    }

    public void roll(float r) {
        roll += r;
    }
}

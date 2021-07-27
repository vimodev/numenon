package engine;

import engine.entities.Player;
import engine.world.Terrain;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.CallbackI;
import utility.Config;
import utility.Global;

import java.nio.DoubleBuffer;

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

    // Movement stuff
    private boolean mouseLocked = false;
    double mouseCenterX = Config.VIEW_WIDTH / 2;
    double mouseCenterY = Config.VIEW_HEIGHT / 2;

    /**
     * Create a new camera
     */
    public Camera() {
        position = new Vector3f(0);
        projection = new Matrix4f();
        setProjection((float) Config.VIEW_WIDTH / (float) Config.VIEW_HEIGHT, 70, 0.1f, 10000f);
    }

    public void lookAt(Vector3f center) {
        Vector3f direction = center.sub(position, new Vector3f());
        direction.normalize();
        pitch = (float) -Math.toDegrees(Math.asin(direction.y));
        yaw = (float) Math.toDegrees(Math.atan2(direction.x, -direction.z));
        roll = 0;
    }

    public void follow(Player player, Terrain terrain, float distance, float height) {
        Vector3f dir = player.getDirection();
        dir.mul(-1);
        dir.normalize();
        dir.mul(distance);
        Vector3f playerPosition = player.getPosition();
        float finalY = Math.max(
                player.getPosition().y + height,
                terrain.sample(dir.x + playerPosition.x, dir.z + playerPosition.z) + 1f
        );
        setPosition(new Vector3f(dir.x + playerPosition.x, finalY, dir.z + playerPosition.z));
    }

    public void terrainBoundMove(Terrain terrain, double dt) {
        freeMove(dt);
        float ground = terrain.sample(position.x, position.z);
        if (position.y < ground + 1.8f) position.y = ground + 1.8f;
    }

    public void freeMove(double dt) {
        // Mouse looking
        if (!mouseLocked && glfwGetMouseButton(Global.WINDOW_IDENTIFIER, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
            glfwSetCursorPos(Global.WINDOW_IDENTIFIER, mouseCenterX, mouseCenterY);
            mouseLocked = true;
        } else if (mouseLocked) {
            DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(Global.WINDOW_IDENTIFIER, x, y);
            x.rewind();
            y.rewind();
            double newX = x.get();
            double newY = y.get();
            double deltaX = newX - Config.VIEW_WIDTH / 2;
            double deltaY = newY - Config.VIEW_HEIGHT / 2;
            //System.out.println("Delta X = " + deltaX + " Delta Y = " + deltaY);
            glfwSetCursorPos(Global.WINDOW_IDENTIFIER, Config.VIEW_WIDTH/2, Config.VIEW_HEIGHT/2);
            pitch((float) deltaY * Config.CAMERA_MOUSE_SENS);
            yaw((float) deltaX * Config.CAMERA_MOUSE_SENS);
            pitch = Math.max(pitch, -90f); pitch = Math.min(pitch, 90f);
        }
        if (glfwGetMouseButton(Global.WINDOW_IDENTIFIER, GLFW_MOUSE_BUTTON_1) != GLFW_PRESS) {
            mouseLocked = false;
        }
        // Keyboard movement
        float mv_scl_forward = glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_W) - glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_S);
        Vector3f direction = getDirection();
        translate(direction.mul(mv_scl_forward * Config.CAMERA_MOVE_SPEED * (float) dt));
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
//        // Get the transformation
//        Vector4f v = new Vector4f(0, 0, -1, 1);
//        Matrix4f M = getTransformation();
//        M.translate(position);
//        // Apply to vector
//        v.mul(M);
//        // Extract direction
//        return (new Vector3f(-v.x, -v.y, v.z)).normalize();
        // Apply transformations
        Vector3f dir = new Vector3f(0, 0, -1);
        dir.rotateX((float) Math.toRadians(-pitch));
        dir.rotateY((float) Math.toRadians(-yaw));
        dir.rotateZ((float) Math.toRadians(roll));
        return dir;
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

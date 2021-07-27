package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import engine.graphics.shaders.TextureShader;
import engine.world.Terrain;
import org.joml.Vector3f;
import utility.Config;
import utility.Global;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a Player in the game, extra functionality for movement and physics
 */
public class Player extends Entity {

    /**
     * The player's velocity
     */
    private Vector3f velocity;

    public Player(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel("warrior.obj", "");
        this.shader = new TextureShader();
        this.material = new Material(new Vector3f(1), new Vector3f(1));
        this.velocity = new Vector3f(0);
    }

    /**
     * Update the player, every frame
     * @param dt time passed since last frame
     * @param terrain the terrain to take into account
     */
    public void update(double dt, Terrain terrain) {
        applyGravity(dt);
        applyJump(dt, terrain);
        applyMovement(dt);
        applyVelocity(dt);
        applyGeneralFriction(dt);
        checkTerrainCollision(terrain);
    }

    /**
     * Get the direction the player is facing
     * @return
     */
    public Vector3f getDirection() {
        Vector3f dir = new Vector3f(0, 0, -1);
        dir.rotateX((float) Math.toRadians(-rotation.x));
        dir.rotateY((float) -Math.toRadians(-rotation.y));
        dir.rotateZ((float) Math.toRadians(rotation.z));
        return dir;
    }

    /**
     * Apply friction to the players horizontal velocity
     * @param dt
     */
    private void applyGeneralFriction(double dt) {
        float factor = 1 - (float) dt * Config.PHYSICS_GENERAL_FRICTION;
        if (factor < 0) factor = 0;
        velocity.x *= factor; velocity.z *= factor;
    }

    /**
     * Gather input and apply movement accordingly
     * @param dt
     */
    private void applyMovement(double dt) {
        Vector3f direction = getDirection();
        float mv_scl_forward = glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_W) - glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_S);
        Vector3f velUpdate = direction.mul(mv_scl_forward * Config.PLAYER_ACCELERATION * (float) dt);
        velUpdate.y = 0;
        velocity.add(velUpdate);
        float newHorizontalVelocity = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (newHorizontalVelocity > Config.PLAYER_MOVE_SPEED) {
            float normalize = Config.PLAYER_MOVE_SPEED / newHorizontalVelocity;
            velocity.x *= normalize; velocity.y *= normalize;
        }
        //translate(direction.mul(mv_scl_forward * Config.PLAYER_MOVE_SPEED * (float) dt));
        float turn_scl = glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_A) - glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_D);
        rotate(new Vector3f(0, turn_scl * Config.PLAYER_TURN_SPEED * (float) dt, 0));
    }

    /**
     * Get input and check if we have to jump or not
     * @param dt
     * @param terrain
     */
    private void applyJump(double dt, Terrain terrain) {
        float y = terrain.sample(position.x, position.z);
        if (position.y <= y && glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_SPACE) == 1) {
            velocity.y += Config.PLAYER_JUMP_SPEED;
        }
    }

    /**
     * With the newly calculated velocity for this frame,
     * apply the velocity to the position based on time passed
     * @param dt
     */
    private void applyVelocity(double dt) {
        Vector3f appliedVelocity = new Vector3f();
        velocity.mul((float) dt, appliedVelocity);
        position.add(velocity);
    }

    /**
     * Apply gravity to the velocity
     * @param dt
     */
    private void applyGravity(double dt) {
        velocity.y -= dt * Config.PHYSICS_GRAVITY;
    }

    /**
     * Check for collision with terrain,
     * if so, y-velocity set to 0
     * @param terrain
     */
    private void checkTerrainCollision(Terrain terrain) {
        float y = terrain.sample(position.x, position.z);
        if (position.y < y) {
            position.y = y;
            velocity.y = 0;
        }
    }

}
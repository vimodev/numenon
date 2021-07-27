package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import engine.graphics.shaders.TextureShader;
import engine.world.Terrain;
import org.joml.Vector3f;
import utility.Config;
import utility.Global;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {

    private Vector3f velocity;

    public Player(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel("warrior.obj", "");
        this.shader = new TextureShader();
        this.material = new Material(new Vector3f(1), new Vector3f(1));
        this.velocity = new Vector3f(0);
    }

    public void update(double dt, Terrain terrain) {
        applyGravity(dt);
        applyJump(dt, terrain);
        applyMovement(dt);
        applyVelocity(dt);
        applyGeneralFriction(dt);
        checkTerrainCollision(terrain);
    }

    public Vector3f getDirection() {
        Vector3f dir = new Vector3f(0, 0, -1);
        dir.rotateX((float) Math.toRadians(-rotation.x));
        dir.rotateY((float) -Math.toRadians(-rotation.y));
        dir.rotateZ((float) Math.toRadians(rotation.z));
        return dir;
    }

    private void applyGeneralFriction(double dt) {
        float factor = 1 - (float) dt * Config.PHYSICS_GENERAL_FRICTION;
        if (factor < 0) factor = 0;
        velocity.x *= factor; velocity.z *= factor;
    }

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

    private void applyJump(double dt, Terrain terrain) {
        float y = terrain.sample(position.x, position.z);
        if (position.y <= y && glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_SPACE) == 1) {
            velocity.y += Config.PLAYER_JUMP_SPEED;
        }
    }

    private void applyVelocity(double dt) {
        Vector3f appliedVelocity = new Vector3f();
        velocity.mul((float) dt, appliedVelocity);
        position.add(velocity);
    }

    private void applyGravity(double dt) {
        velocity.y -= dt * Config.PHYSICS_GRAVITY;
    }

    private void checkTerrainCollision(Terrain terrain) {
        float y = terrain.sample(position.x, position.z);
        if (position.y < y) {
            position.y = y;
            velocity.y = 0;
        }
    }

}

package engine.entities;

import collada.colladaLoader.ColladaLoader;
import engine.Loader;
import engine.graphics.Material;
import engine.graphics.models.AnimatedModel;
import engine.graphics.shaders.AnimatedTextureShader;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TextureShader;
import engine.world.Terrain;
import engine.world.Water;
import engine.world.World;
import game.InputController;
import org.joml.Vector3f;
import utility.Config;
import utility.Global;
import utility.Timer;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a Player in the game, extra functionality for movement and physics
 */
public class Player extends Entity {

    /**
     * The player's velocity
     */
    private Vector3f velocity;
    private Vector3f previousPosition;
    private Timer collisionTimer;

    public Player(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        //this.model = Loader.loadModel(this,"warrior_cleaned.obj", "stone.png");
        this.model = Loader.loadAnimatedModel("model.dae", "player.png");
        if (this.model instanceof AnimatedModel) {
            ((AnimatedModel) model).doAnimation(Loader.loadAnimation("model.dae"), true);
        }
        this.material = new Material(new Vector3f(1), new Vector3f(1));
        this.velocity = new Vector3f(0);
        this.previousPosition = position;
        this.collisionTimer = new Timer();
    }

    /**
     * Update the player, every frame
     * @param dt time passed since last frame
     * @param world the world to take into account
     */
    public void update(double dt, World world) {
        if (this.model instanceof AnimatedModel) {
            float terrainHeight = world.getTerrain().sample(position.x, position.z);
            float fallingFactor = position.y <= terrainHeight + 0.1f ? 1f : 0.2f;
            ((AnimatedModel) model).update(dt * ((getHorizontalVelocity() + 0.15f) / Config.PLAYER_MOVE_SPEED) * fallingFactor);
        }
        Terrain terrain = world.getTerrain();
        applyGravity(dt);
        applyWaterBuoyancy(dt, world.getWater());
        applyJump(terrain);
        applyMovement(dt);
        applyVelocity(dt);
        applyMovementFriction(dt, terrain, world.getWater());
        checkTerrainCollision(terrain, dt);
        if(!checkEntityCollision(world.getCollisionCheckedEntities())) {
            previousPosition = new Vector3f(position.x, position.y, position.z);
        }
    }

    private void applyWaterBuoyancy(double dt, Water water) {
        float level = water.getLevel();
        if (position.y < level) {
            float diff = level - position.y;
            velocity.y += diff * Config.PHYSICS_BUOYANCY * (float) dt;
        }
    }

    private boolean checkEntityCollision(List<Entity> candidates) {
        for (Entity entity : candidates) {
            if (entity.isColliding(position)) {
                position = new Vector3f(previousPosition.x, previousPosition.y, previousPosition.z);
                Vector3f dir = position.sub(entity.getPosition(), new Vector3f());
                dir.normalize();
                dir.mul(Config.ENTITY_COLLISION_BOUNCEBACK);
                velocity = dir;
                collisionTimer.dt();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isColliding(Vector3f checkedPosition) {
        return false;
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

    public float getHorizontalVelocity() {
        return (float) Math.sqrt((velocity.x * velocity.x) + (velocity.z * velocity.z));
    }

    /**
     * Apply friction to the players horizontal velocity
     * @param dt
     */
    private void applyMovementFriction(double dt, Terrain terrain, Water water) {
        float y = terrain.sample(position.x, position.z);
        if (position.y > y + 0.1f && position.y > water.getLevel()) return;
        float ratio = getHorizontalVelocity() / Config.PLAYER_MOVE_SPEED;
        float friction = (float) Math.pow(ratio, 1f / Config.PLAYER_FRICTION_SMOOTHNESS) - Config.PLAYER_FRICTION_AMOUNT;
        if (friction < 0) friction = 0;
        friction = (float) Math.pow(friction, dt);
        velocity.x *= friction; velocity.z *= friction;
    }

    /**
     * Gather input and apply movement accordingly
     * @param dt
     */
    private void applyMovement(double dt) {
        boolean cooldown = (collisionTimer.readDt() <= Config.ENTITY_COLLISION_TIMEOUT);
        Vector3f direction = getDirection();
        float mv_scl_forward = InputController.keyHeldInt(GLFW_KEY_W) - InputController.keyHeldInt(GLFW_KEY_S);
        if (mv_scl_forward != 0 && !cooldown) {
            direction.mul(Config.PLAYER_MOVE_SPEED * mv_scl_forward);
            direction.y = velocity.y;
            velocity.set(direction);
        }
        float turn_scl = InputController.keyHeldInt(GLFW_KEY_A) - InputController.keyHeldInt(GLFW_KEY_D);
        rotate(new Vector3f(0, turn_scl * Config.PLAYER_TURN_SPEED * (float) dt, 0));
    }

    /**
     * Get input and check if we have to jump or not
     * @param terrain
     */
    private void applyJump(Terrain terrain) {
        float y = terrain.sample(position.x, position.z);
        if (position.y <= y && InputController.keyPressed(GLFW_KEY_SPACE)) {
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
        position.add(appliedVelocity);
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
    private void checkTerrainCollision(Terrain terrain, double dt) {
        float y = terrain.sample(position.x, position.z);
        if (position.y < y) {
            position.y = y;
            velocity.y = 0;
        }
    }

}

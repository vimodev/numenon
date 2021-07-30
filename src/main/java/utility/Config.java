package utility;

/**
 * Utility to declare global configs
 */
public class Config {

    public static String SHADER_LOCATION = "/shaders/";
    public static String TEXTURE_LOCATION = "/textures/";
    public static String MODEL_LOCATION = "/models/";
    public static String HEIGHTMAP_LOCATION = "/heightmaps/";
    public static String FONT_LOCATION = "/fonts/";
    public static String DEFAULT_TEXTURE = "default.png";

    public static int VIEW_WIDTH = 1920;
    public static int VIEW_HEIGHT = 1080;

    public static float CAMERA_MOVE_SPEED = 100f;
    public static float CAMERA_MOUSE_SENS = 0.2f;

    public static final float ENTITY_VIEW_DISTANCE = 75f;
    public static final float ENTITY_COLLISION_CHECK_RADIUS = 5f;
    public static final float ENTITY_COLLISION_BOUNCEBACK = 3f;
    public static final float ENTITY_COLLISION_TIMEOUT = 0.5f;

    public static final float PLAYER_MOVE_SPEED = 5f;
    public static float PLAYER_TURN_SPEED = 270f;
    public static float PLAYER_JUMP_SPEED = 5f;
    public static float PLAYER_FRICTION_SMOOTHNESS = 4f;
    public static float PLAYER_FRICTION_AMOUNT = 0.95f;

    public static float PHYSICS_GRAVITY = 9.81f;
    public static float PHYSICS_BUOYANCY = 20f;

}

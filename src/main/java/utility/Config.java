package utility;

/**
 * Utility to declare global configs
 */
public class Config {

    public static String SHADER_LOCATION = "/shaders/";
    public static String TEXTURE_LOCATION = "/textures/";
    public static String MODEL_LOCATION = "/models/";
    public static String HEIGHTMAP_LOCATION = "/heightmaps/";
    public static String DEFAULT_TEXTURE = "default.png";

    public static int VIEW_WIDTH = 1920;
    public static int VIEW_HEIGHT = 1080;

    public static float CAMERA_MOVE_SPEED = 100f;
    public static float CAMERA_MOUSE_SENS = 0.2f;

    public static float PLAYER_MOVE_SPEED = 15f;
    public static float PLAYER_ACCELERATION = 5f;
    public static float PLAYER_TURN_SPEED = 270f;
    public static float PLAYER_JUMP_SPEED = 1.25f;

    public static float PHYSICS_GRAVITY = 5f;
    public static float PHYSICS_GENERAL_FRICTION = 5f;

}

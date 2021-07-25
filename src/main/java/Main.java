import engine.Camera;
import engine.Light;
import engine.Loader;
import engine.World;
import engine.entities.Entity;
import engine.entities.TestEntity;
import engine.graphics.Renderer;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import utility.Config;
import utility.Global;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private Window window;
    private final int WINDOW_WIDTH = Config.VIEW_WIDTH;
    private final int WINDOW_HEIGHT = Config.VIEW_HEIGHT;
    private final String WINDOW_TITLE = "Numenon";

    private Timer frameTimer;

    /**
     * Run the game
     */
    public void run() {
        init();
        loop();
        terminate();
    }

    /**
     * Initialize the game
     */
    public void init() {
        // Print glfw errors
        GLFWErrorCallback.createPrint(System.err).set();
        // If GLFW failed to initialize, throw exception
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }
        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Create the game window
        window = new Window(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);
        Global.WINDOW_IDENTIFIER = window.getWindow();
        // Center the window
        glfwSetWindowPos(
                window.getWindow(),
                (vidmode.width() - WINDOW_WIDTH) / 2,
                (vidmode.height() - WINDOW_HEIGHT) / 2
        );
        // Escape closes window
        glfwSetKeyCallback(window.getWindow(), (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });
        // Make the OpenGL context current
        glfwMakeContextCurrent(window.getWindow());
        // Make the window visible
        glfwShowWindow(window.getWindow());
        // Enable vsync
        glfwSwapInterval(1);
        // Allows us to use OpenGL
        GL.createCapabilities();
        // Set the background clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // Initialize frame timer
        frameTimer = new Timer();
    }

    /**
     * Main game loop
     */
    public void loop() {

        World world = new World("test");
        Entity entity = new TestEntity("test", new Vector3f(0, 0, -100),new Vector3f(5), new Vector3f(0));
        Entity entity2 = new TestEntity("test2", new Vector3f(-50, 5, -100),new Vector3f(5), new Vector3f(0));
        Camera camera = new Camera();
        world.setCamera(camera);
        world.addEntity(entity);
        world.addEntity(entity2);
        world.addLight(new Light("light_0", new Vector3f(0, 10, 0), new Vector3f(0.01f), new Vector3f(1f), new Vector3f(0)));
        world.addLight(new Light("light_1", new Vector3f(0, 10, -150), new Vector3f(0.01f), new Vector3f(1f), new Vector3f(0)));

        double dt = 0;
        while ( !glfwWindowShouldClose(window.getWindow()) ) {
            dt = frameTimer.dt();

            Renderer.pre();

            camera.move();
            entity.rotate(new Vector3f(0, 1f, 0));
            Renderer.render(world);

            glfwSwapBuffers(window.getWindow()); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    /**
     * Terminate the game
     */
    public void terminate() {
        Loader.cleanUp();
        // Clear any window callbacks
        glfwFreeCallbacks(window.getWindow());
        // Destroy the window
        glfwDestroyWindow(window.getWindow());
        // Terminate GLFW
        glfwTerminate();
        // Exit the process
        System.exit(0);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

}

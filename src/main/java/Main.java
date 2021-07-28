import engine.Loader;
import engine.Window;
import engine.gui.GUI;
import engine.gui.GUIElement;
import engine.gui.GUIRenderer;
import engine.world.World;
import engine.graphics.Renderer;
import engine.world.WorldBuilder;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import utility.Config;
import utility.Global;
import utility.Timer;

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

        World world = WorldBuilder.testWorld1();
        GUI gui = new GUI();
        gui.addElement(new GUIElement("mylogo.png", new Vector2f(-0.85f, -0.85f), new Vector2f(0.15f)));

        double dt = 0; double accum = 0;
        while ( !glfwWindowShouldClose(window.getWindow()) ) {
            dt = frameTimer.dt();

            if (glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_F10) == 1) {
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            }
            if (glfwGetKey(Global.WINDOW_IDENTIFIER, GLFW_KEY_F11) == 1) {
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            }

            Renderer.pre();
            world.tick(dt);
            Renderer.render(world);
            GUIRenderer.render(gui);

            accum += dt;
            if (accum >= 5) {
                accum -= 5;
                System.out.println(frameTimer.fps());
            }

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

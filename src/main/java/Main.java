import engine.Loader;
import engine.graphics.Model;
import engine.graphics.Renderer;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TestShader;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private Window window;
    private final int WINDOW_WIDTH = 1920;
    private final int WINDOW_HEIGHT = 1080;
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

        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
        };
        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };
        Model model = Loader.loadToVAO(vertices, indices);
        Shader shader = new TestShader();

        double dt = 0;
        while ( !glfwWindowShouldClose(window.getWindow()) ) {
            dt = frameTimer.dt();

            Renderer.pre();
            shader.use();
            Renderer.render(model);
            shader.unuse();

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

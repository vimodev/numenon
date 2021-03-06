import engine.Loader;
import engine.Window;
import engine.gui.GUI;
import engine.gui.GUIElement;
import engine.gui.fonts.fontMeshCreator.FontType;
import engine.gui.fonts.fontMeshCreator.GUIText;
import engine.world.World;
import engine.world.WorldBuilder;
import game.InputController;
import game.Inventory;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import utility.Config;
import utility.Global;
import utility.Timer;

import java.awt.*;

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
        InputController.initialize();
    }

    /**
     * Main game loop
     */
    public void loop() {
        World world = WorldBuilder.testWorld1();
        //GUI.addElement(Inventory.getGuiElement());

        FontType font = new FontType(Loader.loadTexture("verdana.png").getTextureID(), Loader.loadFontFile("verdana.fnt"));
        GUIText text = new GUIText("fps","0", 1, font, new Vector2f(0f), 1f, false);
        text.setColour(1, 1, 1);

        boolean wireframeMode = false;

        double dt = 0; double accum = 0;
        while ( !glfwWindowShouldClose(window.getWindow()) ) {
            dt = frameTimer.dt();

            if (InputController.keyPressed(GLFW_KEY_F10)) {
                if (wireframeMode) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    wireframeMode = false;
                } else {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    wireframeMode = true;
                }
            }

            world.tick(dt);
            world.render();
            GUI.render();

            accum += dt;
            if (accum >= 1) {
                accum -= 1;
                Loader.handleTerrainQueue();
                Loader.handleEntityQueue();
                text.setContent(String.format("%.2f", frameTimer.fps()));
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

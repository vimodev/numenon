package engine.world;

import engine.Camera;
import engine.Loader;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import utility.Global;
import utility.Timer;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

/**
 * This job always runs in the background
 * it checks the camera position in the world and
 * generates new terrain if necessary
 */
public class GenerateNeighbours implements Runnable {

    private World world;
    private Timer timer;

    public GenerateNeighbours(World world) {
        this.world = world;
        this.timer = new Timer();
    }

    /**
     * Given offset, get index
     * @param offset
     * @return
     */
    private int indexFromRelativeOffset(Vector2i offset) {
        if (offset.equals(-1, 0)) return 3;
        if (offset.equals(1, 0)) return 4;
        int index = 1;
        if (offset.y == 1) index += 5;
        index += offset.x;
        return index;
    }

    /**
     * Given index, get offset, cumulative
     * @param i
     * @return
     */
    private Vector2i offsetFromIndex(int i) {
        Vector2i offset = world.terrain.getOffsets();
        if (i <= 2) offset.y--;
        if (i >= 5) offset.y++;
        if (i == 0 || i == 3 || i == 5) offset.x--;
        if (i == 2 || i == 4 || i == 7) offset.x++;
        return offset;
    }

    /**
     * Given index, get offset, relative to current center
     * @param i
     * @return
     */
    private Vector2i relativeOffsetFromIndex(int i) {
        Vector2i offset = new Vector2i(0);
        if (i <= 2) offset.y--;
        if (i >= 5) offset.y++;
        if (i == 0 || i == 3 || i == 5) offset.x--;
        if (i == 2 || i == 4 || i == 7) offset.x++;
        return offset;
    }

    @Override
    public void run() {
        // First generate initial neighbours
        Terrain original = world.getTerrain();
        for (int i = 0; i < 8; i++) {
            // Get the offset
            Vector2i offset = offsetFromIndex(i);
            // Get the translation we need to do in world space
            Vector3f translation = new Vector3f(
                    original.getPosition().x + relativeOffsetFromIndex(i).x * original.getWidth(),
                    original.getPosition().y,
                    original.getPosition().z + relativeOffsetFromIndex(i).y * original.getHeight()
            );
            // Add the neighbour
            world.neighbours[i] = new Terrain(original.getWidth(), original.getHeight(),
                    original.getyScale(), original.getResolution(), original.getPosition().add(translation, new Vector3f()),
                    original.getTexturePack(), offset.x, offset.y, world.getWater().getLevel());
        }
        // Loop indefinitely
        while (true) {
            // Do some sleeping
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Camera camera = world.getCamera();
            Vector3f cpos = camera.getPosition();
            Terrain center = world.getTerrain();
            Vector3f tpos = center.getPosition();
            Terrain[] arr = world.getNeighbours();
            // If player is out of bounds of center map, start moving around stuff and generating
            if (Math.abs(cpos.x - tpos.x) > Math.abs(center.getWidth() / 2) ||
                    Math.abs(cpos.z - tpos.z) > Math.abs(center.getHeight() / 2)) {
                // Determine player offset
                int x_offset = 0;
                int z_offset = 0;
                if (cpos.x - tpos.x > center.getWidth() / 2) x_offset = 1;
                if (cpos.x - tpos.x < -center.getWidth() / 2) x_offset = -1;
                if (cpos.z - tpos.z > center.getHeight() / 2) z_offset = -1;
                if (cpos.z - tpos.z < -center.getHeight() / 2) z_offset = 1;
                if (x_offset == 0 && z_offset == 0) continue;
                int index = indexFromRelativeOffset(new Vector2i(x_offset, z_offset));
                /**
                 * 0 1 2
                 * 3 c 4
                 * 5 6 7
                 */
                // Move stuff to the left
                if (x_offset == 1) {
                    Loader.addToTerrainDestroyQueue(new Terrain[]{arr[0], arr[3], arr[5]});
                    arr[0] = arr[1]; arr[1] = arr[2]; arr[2] = null;
                    arr[3] = center; center = arr[4]; arr[4] = null;
                    arr[5] = arr[6]; arr[6] = arr[7]; arr[7] = null;
                } else if (x_offset == -1) { // Move stuff right
                    Loader.addToTerrainDestroyQueue(new Terrain[]{arr[2], arr[4], arr[7]});
                    arr[2] = arr[1]; arr[1] = arr[0]; arr[0] = null;
                    arr[4] = center; center = arr[3]; arr[3] = null;
                    arr[7] = arr[6]; arr[6] = arr[5]; arr[5] = null;
                }
                // Move stuff down
                if (z_offset == 1) {
                    Loader.addToTerrainDestroyQueue(new Terrain[]{arr[5], arr[6], arr[7]});
                    arr[5] = arr[3]; arr[3] = arr[0]; arr[0] = null;
                    arr[6] = center; center = arr[1]; arr[1] = null;
                    arr[7] = arr[4]; arr[4] = arr[2]; arr[2] = null;
                } else if (z_offset == -1) { // Move stuff up
                    Loader.addToTerrainDestroyQueue(new Terrain[]{arr[0], arr[1], arr[2]});
                    arr[0] = arr[3]; arr[3] = arr[5]; arr[5] = null;
                    arr[1] = center; center = arr[6]; arr[6] = null;
                    arr[2] = arr[4]; arr[4] = arr[7]; arr[7] = null;
                }
                //. Update the new center
                world.setNewCenter(center);
                // Now generate the missing pieces
                for (int i = 0; i < 8; i++) {
                    if (arr[i] == null) {
                        Vector2i offset = offsetFromIndex(i);
                        Vector3f translation = new Vector3f(
                                relativeOffsetFromIndex(i).x * center.getWidth(),
                                center.getPosition().y,
                                relativeOffsetFromIndex(i).y * center.getHeight()
                        );
                        arr[i] = new Terrain(original.getWidth(), original.getHeight(),
                                original.getyScale(), original.getResolution(), center.getPosition().add(translation, new Vector3f()),
                                original.getTexturePack(), offset.x, offset.y, world.getWater().getLevel());
                    }
                }
            }
        }
    }

}

package engine;

import engine.graphics.Model;
import engine.graphics.shaders.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utility.Utility;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for loading assets and tracking objects in memory
 */
public class Loader {

    private static List<Integer> vaos = new ArrayList<Integer>();
    private static List<Integer> vbos = new ArrayList<Integer>();
    private static List<Shader> shaders = new ArrayList<>();

    /**
     * Given attributes load to a Model
     * @param positions
     * @param indices
     * @return
     */
    public static Model loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, positions);
        unbindVAO();
        return new Model(vaoID, indices.length);
    }

    /**
     * Clean up all tracked assets
     */
    public static void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
        for (Shader shader : shaders) {
            shader.cleanUp();
        }
    }

    /**
     * Track the given shader for clean up
     * @param shader
     */
    public static void declareShader(Shader shader) {
        shaders.add(shader);
    }

    /**
     * Create a new VAO
     * @return
     */
    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        bindVAO(vaoID);
        return vaoID;
    }

    /**
     * Given an array of float data, bind it to an attribute
     * @param attributeNumber
     * @param data
     */
    private static void storeDataInAttributeList(int attributeNumber, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = Utility.floatArrayToBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void bindVAO(int vaoID) {
        GL30.glBindVertexArray(vaoID);
    }

    private static void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private static void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = Utility.intArrayToBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

}

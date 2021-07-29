package engine;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import engine.graphics.models.Model;
import engine.graphics.Texture;
import engine.graphics.shaders.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utility.Config;
import utility.Utility;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for loading assets and tracking objects in memory
 */
public class Loader {

    private static List<Integer> vaos = new ArrayList<Integer>();
    private static List<Integer> vbos = new ArrayList<Integer>();
    private static List<Shader> shaders = new ArrayList<>();
    private static List<Integer> textures = new ArrayList<>();

    private static Map<String, Texture> loadedTextures = new HashMap<>();
    private static Map<String, Map<String, Model>> loadedModels = new HashMap<>();

    /**
     * Given attributes load to a Model
     * @param positions
     * @param indices
     * @return
     */
    public static Model loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        List<Integer> modelVBOs = new ArrayList<>();
        modelVBOs.add(storeDataInAttributeList(0, 3, positions));
        modelVBOs.add(storeDataInAttributeList(1, 2, textureCoordinates));
        modelVBOs.add(storeDataInAttributeList(2, 3, normals));
        unbindVAO();
        return new Model(vaoID, indices.length, modelVBOs);
    }

    public static Model loadToVAO(float[] positions, float[] textureCoordinates) {
        int vaoID = createVAO();
        List<Integer> modelVBOs = new ArrayList<>();
        modelVBOs.add(storeDataInAttributeList(0, 2, positions));
        modelVBOs.add(storeDataInAttributeList(1, 2, textureCoordinates));
        unbindVAO();
        return new Model(vaoID, positions.length, modelVBOs);
    }

    public static Model loadToVAO(float[] positions) {
        int vaoID = createVAO();
        List<Integer> modelVBOs = new ArrayList<>();
        modelVBOs.add(storeDataInAttributeList(0, 2, positions));
        unbindVAO();
        return new Model(vaoID, positions.length / 2, modelVBOs);
    }

    public static Model loadModel(String objFile, String textureFile) {
        // If model with that model and texture exists already, return it.
        if (loadedModels.containsKey(objFile) && loadedModels.get(objFile).containsKey(textureFile)) {
            return loadedModels.get(objFile).get(textureFile);
        }
        // Open the resource from file system
        InputStream objInputStream = null;
        Obj object = null;
        try {
            objInputStream = new BufferedInputStream(
                                Loader.class.getResource(Config.MODEL_LOCATION + objFile)
                                    .openStream()
                            );
            object = ObjReader.read(objInputStream);
        } catch (Exception e) {
            System.err.println("Could not read obj file.");
            e.printStackTrace();
        }

        // Make sure OpenGL can render it
        // OpenGL cant handle double normals for instance
        object = ObjUtils.convertToRenderable(object);
        Model model = loadToVAO(ObjData.getVerticesArray(object),
                        ObjData.getTexCoordsArray(object, 2, true),
                        ObjData.getNormalsArray(object),
                        ObjData.getFaceVertexIndicesArray(object, 3));
        // Handle unspecified texture
        if (textureFile != null && textureFile != "") {
            model.setTexture(loadTexture(textureFile));
            textureFile = Config.DEFAULT_TEXTURE;
        }
        // Track load
        if (!loadedModels.containsKey(objFile)) {
            loadedModels.put(objFile, new HashMap<>());
            loadedModels.get(objFile).put(textureFile, model);
        }
        return model;
    }

    public static File loadFontFile(String filename) {
        return new File(Loader.class.getResource(Config.FONT_LOCATION + filename).getFile());
    }

    /**
     * Loads a given texture if it has not been loaded yet
     * @param fileName
     * @return
     */
    public static Texture loadTexture(String fileName) {
        // If texture has not been loaded before
        if (loadedTextures.containsKey(fileName)) {
            // load it
            return loadedTextures.get(fileName);
        }
        // Create texture object
        Texture texture = new Texture(fileName);
        // Update the mapping
        loadedTextures.put(fileName, texture);
        int textureID = texture.getTextureID();
        textures.add(textureID);
        return texture;
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
        for (int texture : textures) {
            GL30.glDeleteTextures(texture);
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
    private static int storeDataInAttributeList(int attributeNumber, int dimensions, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = Utility.floatArrayToBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vboID;
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

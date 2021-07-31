package engine;

import collada.colladaLoader.ColladaLoader;
import collada.dataStructures.AnimatedModelData;
import collada.dataStructures.AnimationData;
import collada.dataStructures.MeshData;
import collada.dataStructures.SkeletonData;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import engine.animation.Animation;
import engine.entities.Entity;
import engine.graphics.models.AnimatedModel;
import engine.graphics.models.Model;
import engine.graphics.Texture;
import engine.graphics.shaders.Shader;
import engine.world.EntityQueueItem;
import engine.world.Terrain;
import engine.world.TerrainQueueItem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utility.Config;
import utility.Global;
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

    private static List<TerrainQueueItem> terrainQueue = new ArrayList<>();
    private static List<Terrain> terrainDestroyQueue = new ArrayList<>();
    private static List<EntityQueueItem> entityQueue = new ArrayList<>();

    private static List<Integer> vaos = new ArrayList<Integer>();
    private static List<Integer> vbos = new ArrayList<Integer>();
    private static List<Shader> shaders = new ArrayList<>();
    private static List<Integer> textures = new ArrayList<>();

    private static Map<String, Texture> loadedTextures = new HashMap<>();
    private static Map<String, Map<String, Model>> loadedModels = new HashMap<>();

    public static void addToTerrainQueue(TerrainQueueItem item) {
        terrainQueue.add(item);
    }
    public static void addToTerrainDestroyQueue(Terrain terrain) {
        terrainDestroyQueue.add(terrain);
    }
    public static void addToTerrainDestroyQueue(Terrain[] terrains) {
        for (Terrain terrain : terrains) {
            terrainDestroyQueue.add(terrain);
        }
    }

    public static void handleTerrainQueue() {
        Global.terrain_queue_mutex.lock();
        for (TerrainQueueItem item : terrainQueue) {
            item.target.setModel(loadToVAO(item.vertices, item.textureCoords, item.normals, item.indices));
            item.target.readyToRender = true;
        }
        terrainQueue.clear();
        for (Terrain terrain : terrainDestroyQueue) {
            Terrain.destroy(terrain);
        }
        terrainDestroyQueue.clear();
        Global.terrain_queue_mutex.unlock();
    }

    public static void addToEntityQueue(EntityQueueItem item) {
        entityQueue.add(item);
    }

    public static void handleEntityQueue() {
        Global.entity_queue_mutex.lock();
        for (EntityQueueItem item : entityQueue) {
            if (loadedModels.containsKey(item.obj) && loadedModels.get(item.obj).containsKey(item.texture)) {
                item.target.setModel(loadedModels.get(item.obj).get(item.texture));
                item.target.readyToRender = true;
                continue;
            }
            Model model = loadToVAO(item.vertices, item.textureCoords, item.normals, item.indices);
            // Handle unspecified texture
            if (item.texture == null || item.texture == "") {
                item.texture = Config.DEFAULT_TEXTURE;
            }
            model.setTexture(loadTexture(item.texture));
            item.target.setModel(model);
            item.target.readyToRender = true;
            // Track load
            if (!loadedModels.containsKey(item.obj)) {
                loadedModels.put(item.obj, new HashMap<>());
            }
            loadedModels.get(item.obj).put(item.obj, model);
        }
        entityQueue.clear();
        Global.entity_queue_mutex.unlock();
    }

    /**
     * Given attributes load to a Model
     * @param positions
     * @param indices
     * @return
     */
    public static synchronized Model loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        List<Integer> modelVBOs = new ArrayList<>();
        modelVBOs.add(storeDataInAttributeList(0, 3, positions));
        modelVBOs.add(storeDataInAttributeList(1, 2, textureCoordinates));
        modelVBOs.add(storeDataInAttributeList(2, 3, normals));
        unbindVAO();
        return new Model(vaoID, indices.length, modelVBOs);
    }

    public static synchronized AnimatedModel loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices,
                                                       int[] joints, float[] weights) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        List<Integer> modelVBOs = new ArrayList<>();
        modelVBOs.add(storeDataInAttributeList(0, 3, positions));
        modelVBOs.add(storeDataInAttributeList(1, 2, textureCoordinates));
        modelVBOs.add(storeDataInAttributeList(2, 3, normals));
        modelVBOs.add(storeDataInAttributeList(3, 3, joints));
        modelVBOs.add(storeDataInAttributeList(4, 3, weights));
        unbindVAO();
        return new AnimatedModel(vaoID, indices.length, modelVBOs);
    }

    public static synchronized Model loadToVAO(float[] positions, float[] textureCoordinates) {
        int vaoID = createVAO();
        List<Integer> modelVBOs = new ArrayList<>();
        modelVBOs.add(storeDataInAttributeList(0, 2, positions));
        modelVBOs.add(storeDataInAttributeList(1, 2, textureCoordinates));
        unbindVAO();
        return new Model(vaoID, positions.length, modelVBOs);
    }

    public static synchronized Model loadToVAO(float[] positions) {
        int vaoID = createVAO();
        List<Integer> modelVBOs = new ArrayList<>();
        modelVBOs.add(storeDataInAttributeList(0, 2, positions));
        unbindVAO();
        return new Model(vaoID, positions.length / 2, modelVBOs);
    }

    public static synchronized Animation loadAnimation(String filePath) {
        File file = new File(Loader.class.getResource(Config.MODEL_LOCATION + filePath).getFile());
        AnimationData data = ColladaLoader.loadColladaAnimation(file);
        Animation animation = new Animation(data.lengthSeconds, data.getEngineKeyframes());
        return animation;
    }

    public static synchronized AnimatedModel loadAnimatedModel(String file, String texture) {
        AnimatedModelData dat = ColladaLoader.loadColladaModel(
                new File(Loader.class.getResource(Config.MODEL_LOCATION + file).getFile()),
                3);
        MeshData meshData = dat.getMeshData();
        AnimatedModel model = loadToVAO(meshData.getVertices(), meshData.getTextureCoords(), meshData.getNormals(),
                meshData.getIndices(), meshData.getJointIds(), meshData.getVertexWeights());
        SkeletonData skeletonData = dat.getJointsData();
        model.setJoints(skeletonData.headJoint.toEngineJoint(), skeletonData.jointCount);
        model.setTexture(loadTexture(texture));
        return model;
    }

    public static synchronized Model loadModel(Entity entity, String objFile, String textureFile) {
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
        if (Thread.currentThread().getName() != "main") {
            Global.entity_queue_mutex.lock();
            addToEntityQueue(new EntityQueueItem(entity, objFile, textureFile, ObjData.getVerticesArray(object),
                    ObjData.getTexCoordsArray(object, 2, true),
                    ObjData.getNormalsArray(object),
                    ObjData.getFaceVertexIndicesArray(object, 3)));
            Global.entity_queue_mutex.unlock();
        }
        Model model = loadToVAO(ObjData.getVerticesArray(object),
                        ObjData.getTexCoordsArray(object, 2, true),
                        ObjData.getNormalsArray(object),
                        ObjData.getFaceVertexIndicesArray(object, 3));
        // Handle unspecified texture
        if (textureFile == null || textureFile == "") {
            textureFile = Config.DEFAULT_TEXTURE;
        }
        model.setTexture(loadTexture(textureFile));
        // Track load
        if (!loadedModels.containsKey(objFile)) {
            loadedModels.put(objFile, new HashMap<>());
        }
        loadedModels.get(objFile).put(textureFile, model);
        return model;
    }

    public static synchronized File loadFontFile(String filename) {
        return new File(Loader.class.getResource(Config.FONT_LOCATION + filename).getFile());
    }

    /**
     * Loads a given texture if it has not been loaded yet
     * @param fileName
     * @return
     */
    public static synchronized Texture loadTexture(String fileName) {
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
    public static synchronized void declareShader(Shader shader) {
        shaders.add(shader);
    }

    /**
     * Create a new VAO
     * @return
     */
    private static synchronized int createVAO() {
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
    private static synchronized int storeDataInAttributeList(int attributeNumber, int dimensions, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = Utility.floatArrayToBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vboID;
    }

    private static synchronized int storeDataInAttributeList(int attributeNumber, int dimensions, int[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer buffer = Utility.intArrayToBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL30.glVertexAttribIPointer(attributeNumber, dimensions, GL11.GL_INT, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vboID;
    }

    private static synchronized void bindVAO(int vaoID) {
        GL30.glBindVertexArray(vaoID);
    }

    private static synchronized void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private static synchronized void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = Utility.intArrayToBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

}

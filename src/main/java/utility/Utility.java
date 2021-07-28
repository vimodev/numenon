package utility;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Class containing several useful common utility functions
 */
public class Utility {

    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    /**
     * Given a set of transformations, returns a transformation matrix that performs those
     * @param translation how to translate in the transformation
     * @param rotation how to rotate in the transformation
     * @param scale how to scale in the transformation
     * @return the transformation matrix
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f M = new Matrix4f();
        M.identity();
        M.translate(translation);
        M.rotateX((float) Math.toRadians(rotation.x));
        M.rotateY((float) Math.toRadians(rotation.y));
        M.rotateZ((float) Math.toRadians(rotation.z));
        M.scale(scale);
        return M;
    }

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f M = new Matrix4f();
        M.identity();
        M.translate(new Vector3f(translation, 0));
        M.scale(new Vector3f(scale, 1));
        return M;
    }

    public static FloatBuffer floatArrayToBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static IntBuffer intArrayToBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}

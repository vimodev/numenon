package utility;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Class containing several useful common utility functions
 */
public class Utility {

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

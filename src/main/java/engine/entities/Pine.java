package engine.entities;

import engine.Loader;
import engine.graphics.Material;
import engine.graphics.shaders.Shader;
import engine.graphics.shaders.TextureShader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Pine extends Entity {

    public Pine(String name, Vector3f position, Vector3f scale, Vector3f rotation) {
        super(name, position, scale, rotation);
        this.model = Loader.loadModel(this,"pine.obj", "pine.png");
        this.material = new Material(new Vector3f(1), new Vector3f(1));
    }

    @Override
    public boolean isColliding(Vector3f checkedPosition) {
        // We map the checked position into model space for the pine
        Matrix4f invertedTransformation = getMatrix().invert(new Matrix4f());
        Vector4f modelSpacePosition = (new Vector4f(checkedPosition, 1).mul(invertedTransformation));
        Vector3f point = new Vector3f(modelSpacePosition.x, modelSpacePosition.y, modelSpacePosition.z);
        point.div(modelSpacePosition.w);
        return ((point.x >= -0.86f && point.x <= 0.86f) && (point.z >= -1 && point.z <= 1) && (point.y >= 0 && point.y <= 19));
    }

}

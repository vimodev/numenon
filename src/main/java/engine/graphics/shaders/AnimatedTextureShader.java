package engine.graphics.shaders;

/**
 * Basic color gradient shader for testing purposesTestShader
 */
public class AnimatedTextureShader extends Shader {

    private static final String VERTEX_FILE = "animated_texture_vertex.glsl";
    private static final String FRAGMENT_FILE = "animated_texture_fragment.glsl";

    public AnimatedTextureShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "joints");
        super.bindAttribute(4, "weights");
    }
}

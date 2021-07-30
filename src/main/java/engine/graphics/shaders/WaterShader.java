package engine.graphics.shaders;

/**
 * Basic color gradient shader for testing purposesTestShader
 */
public class WaterShader extends Shader {

    private static final String VERTEX_FILE = "wat_vertex.glsl";
    private static final String FRAGMENT_FILE = "wat_fragment.glsl";

    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }
}

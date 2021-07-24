package engine.graphics.shaders;

/**
 * Basic color gradient shader for testing purposes
 */
public class TestShader extends Shader {

    private static final String VERTEX_FILE = "test_vertex.glsl";
    private static final String FRAGMENT_FILE = "test_fragment.glsl";

    public TestShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}

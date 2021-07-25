package engine.graphics.shaders;

/**
 * Basic color gradient shader for testing purposesTestShader
 */
public class TerrainShader extends Shader {

    private static final String VERTEX_FILE = "terr_vertex.glsl";
    private static final String FRAGMENT_FILE = "terr_fragment.glsl";

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }
}

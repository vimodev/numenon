package engine.gui;

import engine.graphics.shaders.Shader;

public class GUIShader extends Shader {

    private static final String VERTEX_FILE = "gui_vertex.glsl";
    private static final String FRAGMENT_FILE = "gui_fragment.glsl";

    public GUIShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}

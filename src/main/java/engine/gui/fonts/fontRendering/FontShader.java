package engine.gui.fonts.fontRendering;

import engine.graphics.shaders.Shader;

public class FontShader extends Shader {

	private static final String VERTEX_FILE = "font_vertex.glsl";
	private static final String FRAGMENT_FILE = "font_fragment.glsl";
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}


}

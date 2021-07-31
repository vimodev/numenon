package engine.gui;

import engine.Loader;
import engine.graphics.models.Model;
import engine.gui.fonts.fontMeshCreator.FontType;
import engine.gui.fonts.fontMeshCreator.GUIText;
import engine.gui.fonts.fontMeshCreator.TextMeshData;
import engine.gui.fonts.fontRendering.FontRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI {

    private static List<GUIElement> elements = new ArrayList<>();
    private static Map<FontType, List<GUIText>> texts = new HashMap<>();

    public static void render() {
        GUIRenderer.render();
    }

    public static List<GUIElement> getElements() {
        return elements;
    }

    public static void addElement(GUIElement element) {
        elements.add(element);
    }

    public static void removeElement(GUIElement element) {
        elements.remove(element);
    }

    public static void removeElementByName(String name) {
        for (GUIElement element : elements) {
            if (element.getName().equals(name)) {
                removeElement(element);
                return;
            }
        }
    }

    public static Map<FontType, List<GUIText>> getTexts() {
        return texts;
    }

    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        Model model = Loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(model);
        List<GUIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void removeText(GUIText text){
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if(textBatch.isEmpty()){
            texts.remove(texts.get(text.getFont()));
        }
    }

    public static void removeTextByName(String name) {
        for (FontType font : texts.keySet()) {
            for (GUIText text : texts.get(font)) {
                if (text.getName() == name) {
                    removeText(text);
                    return;
                }
            }
        }
    }
}

package engine.gui;

import java.util.ArrayList;
import java.util.List;

public class GUI {

    private static List<GUIElement> elements = new ArrayList<>();

    public static List<GUIElement> getElements() {
        return elements;
    }

    public static void addElement(GUIElement element) {
        elements.add(element);
    }
}

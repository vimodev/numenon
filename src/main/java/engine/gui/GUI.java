package engine.gui;

import java.util.ArrayList;
import java.util.List;

public class GUI {

    private List<GUIElement> elements;

    public GUI() {
        elements = new ArrayList<>();
    }

    public List<GUIElement> getElements() {
        return elements;
    }

    public void addElement(GUIElement element) {
        elements.add(element);
    }
}

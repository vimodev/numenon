package game;

import engine.gui.GUIElement;
import game.items.Item;
import org.joml.Vector2f;
import utility.Config;

public class Inventory {

    protected static final int HORIZONTAL_NR_SLOTS = 9;
    protected static final int VERTICAL_NR_SLOTS = 3;
    protected static Item[][] contents = new Item[HORIZONTAL_NR_SLOTS][VERTICAL_NR_SLOTS];

    protected static GUIElement guiElement = new GUIElement("inventory", "inventory.png",
            new Vector2f(0), new Vector2f(0.5f, 0.5f * Config.ASPECT_RATIO));

    public static Item[][] getContents() {
        return contents;
    }

    public static void clearContents() {
        for (int x = 0; x < HORIZONTAL_NR_SLOTS; x++) {
            for (int y = 0; y < VERTICAL_NR_SLOTS; y++) {
                setContentAt(x, y, null);
            }
        }
    }

    public static void swapContentsAt(int x1, int y1, int x2, int y2) {
        Item item1 = fetchContentAt(x1, y1);
        Item item2 = fetchContentAt(x2, y2);
        setContentAt(x1, y1, item2);
        setContentAt(x2, y2, item1);
    }

    public static Item fetchContentAt(int x, int y) {
        Item item = getContentAt(x, y);
        setContentAt(x, y, null);
        return item;
    }

    public static Item getContentAt(int x, int y) {
        return contents[x][y];
    }

    public static void setContentAt(int x, int y, Item item) {
        contents[x][y] = item;
    }

    public static GUIElement getGuiElement() {
        return guiElement;
    }
}

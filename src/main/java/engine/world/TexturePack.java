package engine.world;

import engine.Loader;
import engine.graphics.Texture;

public class TexturePack {

    private Texture blendMap;
    private Texture backgroundTexture;
    private Texture rTexture;
    private Texture bTexture;
    private Texture gTexture;

    public TexturePack(String blendMap, String backgroundTexture, String rTexture, String gTexture, String bTexture) {
        this.blendMap = Loader.loadTexture(blendMap);
        this.backgroundTexture = Loader.loadTexture(backgroundTexture);
        this.rTexture = Loader.loadTexture(rTexture);
        this.gTexture = Loader.loadTexture(gTexture);
        this.bTexture = Loader.loadTexture(bTexture);
    }

    public TexturePack(Texture blendMap, Texture backgroundTexture, Texture rTexture, Texture gTexture, Texture bTexture) {
        this.blendMap = blendMap;
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.bTexture = bTexture;
        this.gTexture = gTexture;
    }

    public Texture getBlendMap() {
        return blendMap;
    }

    public void setBlendMap(Texture blendMap) {
        this.blendMap = blendMap;
    }

    public Texture getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(Texture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }

    public Texture getrTexture() {
        return rTexture;
    }

    public void setrTexture(Texture rTexture) {
        this.rTexture = rTexture;
    }

    public Texture getbTexture() {
        return bTexture;
    }

    public void setbTexture(Texture bTexture) {
        this.bTexture = bTexture;
    }

    public Texture getgTexture() {
        return gTexture;
    }

    public void setgTexture(Texture gTexture) {
        this.gTexture = gTexture;
    }
}

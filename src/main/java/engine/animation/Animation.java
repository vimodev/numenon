package engine.animation;

public class Animation {

    private float length;
    private KeyFrame[] keyFrames;

    public Animation(float length, KeyFrame[] frames) {
        this.length = length;
        this.keyFrames = frames;
    }

    public float getLength() {
        return length;
    }

    public KeyFrame[] getKeyFrames() {
        return keyFrames;
    }
}

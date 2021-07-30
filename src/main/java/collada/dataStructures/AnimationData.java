package collada.dataStructures;

import engine.animation.KeyFrame;

/**
 * Contains the extracted data for an animation, which includes the length of
 * the entire animation and the data for all the keyframes of the animation.
 * 
 * @author Karl
 *
 */
public class AnimationData {

	public final float lengthSeconds;
	public final KeyFrameData[] keyFrames;

	public AnimationData(float lengthSeconds, KeyFrameData[] keyFrames) {
		this.lengthSeconds = lengthSeconds;
		this.keyFrames = keyFrames;
	}

	public KeyFrame[] getEngineKeyframes() {
		KeyFrame[] list = new KeyFrame[keyFrames.length];
		for (int i = 0; i < keyFrames.length; i++) {
			list[i] = keyFrames[i].toKeyFrame();
		}
		return list;
	}

}

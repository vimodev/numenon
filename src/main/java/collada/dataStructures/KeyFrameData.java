package collada.dataStructures;

import engine.animation.JointTransform;
import engine.animation.KeyFrame;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import utility.Quaternion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyFrameData {

	public final float time;
	public final List<JointTransformData> jointTransforms = new ArrayList<JointTransformData>();
	
	public KeyFrameData(float time){
		this.time = time;
	}
	
	public void addJointTransform(JointTransformData transform){
		jointTransforms.add(transform);
	}

	public KeyFrame toKeyFrame() {
		Map<String, JointTransform> map = new HashMap<>();
		for (JointTransformData data : jointTransforms) {
			Matrix4f mat = data.jointLocalTransform;
			JointTransform trans = new JointTransform(mat.getTranslation(new Vector3f()),
					Quaternion.fromMatrix(mat));
			map.put(data.jointNameId, trans);
		}
		KeyFrame keyFrame = new KeyFrame(time, map);
		return keyFrame;
	}
	
}

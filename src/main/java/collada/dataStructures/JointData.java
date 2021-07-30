package collada.dataStructures;

import engine.animation.Joint;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the extracted data for a single joint in the model. This stores the
 * joint's index, name, and local bind transform.
 * 
 * @author Karl
 *
 */
public class JointData {

	public final int index;
	public final String nameId;
	public final Matrix4f bindLocalTransform;

	public final List<JointData> children = new ArrayList<JointData>();

	public JointData(int index, String nameId, Matrix4f bindLocalTransform) {
		this.index = index;
		this.nameId = nameId;
		this.bindLocalTransform = bindLocalTransform;
	}

	public void addChild(JointData child) {
		children.add(child);
	}

	public Joint toEngineJoint() {
		Joint joint = new Joint(index, nameId, bindLocalTransform);
		for (JointData child : children) {
			joint.addChild(child.toEngineJoint());
		}
		return joint;
	}

}

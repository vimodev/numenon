package engine.graphics.models;

import engine.animation.Animation;
import engine.animation.Animator;
import engine.animation.Joint;
import org.joml.Matrix4f;

import java.util.List;

public class AnimatedModel extends Model {

    private Joint rootJoint;
    private int jointCount;

    private Animator animator;

    public AnimatedModel(int vaoID, int vertexCount, List<Integer> vbos) {
        super(vaoID, vertexCount, vbos);
        this.animator = new Animator(this);
    }

    public void setJoints(Joint rootJoint, int jointCount) {
        this.rootJoint = rootJoint;
        this.jointCount = jointCount;
        rootJoint.calcInverseBindTransform(new Matrix4f());
    }

    /**
     * @return The root joint of the joint hierarchy. This joint has no parent,
     *         and every other joint in the skeleton is a descendant of this
     *         joint.
     */
    public Joint getRootJoint() {
        return rootJoint;
    }

    /**
     * Instructs this entity to carry out a given animation. To do this it
     * basically sets the chosen animation as the current animation in the
     * {@link Animator} object.
     *
     * @param animation
     *            - the animation to be carried out.
     */
    public void doAnimation(Animation animation, boolean loop) {
        animator.doAnimation(animation, loop);
    }

    /**
     * Updates the animator for this entity, basically updating the animated
     * pose of the entity. Must be called every frame.
     */
    public void update(double dt) {
        animator.update(dt);
    }

    /**
     * Gets an array of the all important model-space transforms of all the
     * joints (with the current animation pose applied) in the entity. The
     * joints are ordered in the array based on their joint index. The position
     * of each joint's transform in the array is equal to the joint's index.
     *
     * @return The array of model-space transforms of the joints in the current
     *         animation pose.
     */
    public Matrix4f[] getJointTransforms() {
        Matrix4f[] jointMatrices = new Matrix4f[jointCount];
        addJointsToArray(rootJoint, jointMatrices);
        return jointMatrices;
    }

    /**
     * This adds the current model-space transform of a joint (and all of its
     * descendants) into an array of transforms. The joint's transform is added
     * into the array at the position equal to the joint's index.
     *
     * @param headJoint
     *            - the current joint being added to the array. This method also
     *            adds the transforms of all the descendents of this joint too.
     * @param jointMatrices
     *            - the array of joint transforms that is being filled.
     */
    private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
        jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
        for (Joint childJoint : headJoint.children) {
            addJointsToArray(childJoint, jointMatrices);
        }
    }

}

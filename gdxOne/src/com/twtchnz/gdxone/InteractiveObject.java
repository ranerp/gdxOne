package com.twtchnz.gdxone;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class InteractiveObject extends ModelInstance implements Dynamic {
    public BoundingBox boundingBox = new BoundingBox();
    public Vector3 coordinates = new Vector3();

    protected boolean movable = true;

    public Node myNode;

    public String nodeId;

    public InteractiveObject(Model model, String id) {
        super(model, new Matrix4(), id, true, false, false);

        nodeId = id;
        myNode = getNode(id, true);
        resetNodeTransformation();
        calculateBoundingBox();
    }

    private void resetNodeTransformation() {
        transform.set(myNode.globalTransform);
        myNode.translation.set(0, 0, 0);
        myNode.scale.set(1, 1, 1);
        myNode.rotation.idt();
        calculateTransforms();
    }

    private void calculateBoundingBox() {
        myNode.calculateBoundingBox(boundingBox);

    }

    public void move(Vector3 vec3) {
    }

    public void update(double deltaTime) {

    }
}

package com.twtchnz.gdxone;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class Sphere extends InteractiveObject {

    public final static float SPEED = 0.2f;
    public final static float WEIGHT = 50f;

    public Vector3 Velocity = new Vector3();

    private float x = 0f;
    private float y = 0f;
    private float z = -0.7f;

    public Sphere(Model model, String id) {
        super(model, id);
        movable = true;

        Velocity.set(x, y, z);

    }

    public void move(Vector3 vec3) {
        coordinates.add(vec3);
        boundingBox.set(boundingBox.min.add(vec3), boundingBox.max.add(vec3));
        transform.setToTranslation(coordinates);

    }


    @Override
    public void update(double deltaTime) {

        Vector3 vec = new Vector3(Velocity.x * (float)deltaTime, Velocity.y * (float)deltaTime, Velocity.z * (float)deltaTime);

        move(vec);
    }

    public void bounce(Vector3 vec) {
        Velocity.set(vec);
    }

}

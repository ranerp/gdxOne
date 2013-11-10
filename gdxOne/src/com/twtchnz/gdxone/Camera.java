package com.twtchnz.gdxone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class Camera {
    private PerspectiveCamera camera;

    private int fov;
    private float positionX, positionY, positionZ;
    private float lookX, lookY, lookZ;
    private float near, far;

    public Camera() {
        fov = 67;
        positionX = 0f;
        positionY = 50f;
        positionZ = 22f;

        lookX = 0;
        lookY = 0;
        lookZ = 0;
        near = 0.1f;
        far = 300f;

        init();
    }

    public Camera(int fov, float  positionX, float positionY,
                  float positionZ, float lookX, float lookY,
                  float lookZ, float near, float far) {
        this.fov = fov;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
        this.near = near;
        this.far = far;

        init();
    }

    private void init(){
        camera = new PerspectiveCamera(fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.position.set(positionX, positionY, positionZ);
        camera.lookAt(lookX, lookY, lookZ);
        camera.near = near;
        camera.far = far;
    }

    public PerspectiveCamera get() {
        return this.camera;
    }

    public void update() {
        this.camera.update();
    }

    public void fov(int fov) {
        this.fov = fov;
    }

    public void near(float near) {
        this.near = near;
    }

    public void far(float far) {
        this.far = far;
    }

    public void position(float x, float y, float z) {
        this.positionX = x;
        this.positionY = y;
        this.positionZ = z;
    }

    public void setLookAt(float x, float y, float z) {
        this.lookX = x;
        this.lookY = y;
        this.lookZ = z;
    }
}

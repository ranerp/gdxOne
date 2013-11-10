package com.twtchnz.gdxone;

import com.badlogic.gdx.math.Vector3;

public interface Dynamic {

    public void move(Vector3 vec);
    public void update(double deltaTime);
}

package com.core.engine;

import com.core.graphics.Camera;
import com.core.graphics.Window;

public abstract class GameObject {
    public abstract void update(double dt);
    public abstract void render(Camera camera, Window window);
    public abstract void cleanup();
}
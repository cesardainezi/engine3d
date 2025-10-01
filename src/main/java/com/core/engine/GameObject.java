package com.core.engine;

import com.core.engine.components.Transform;
import com.core.graphics.material.Material;
import com.core.graphics.mesh.Mesh;
import com.core.graphics.Camera;
import com.core.graphics.Window;

public abstract class GameObject {
    protected Transform transform;
    protected Mesh mesh;
    protected Material material;

    public Transform getTransform() { return transform; }
    public Mesh getMesh() { return mesh; }
    public Material getMaterial() { return material; }

    public abstract void update(double dt);
    public abstract void render(Camera camera, Window window);
    public abstract void cleanup();
}
package com.core.engine.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;

    public Transform(Vector3f position) {
        this.position = position;
        this.rotation = new Vector3f(0,0,0);
        this.scale = new Vector3f(1,1,1);
    }

    public Matrix4f toMatrix() {
        return new Matrix4f().identity()
                .translate(position)
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .rotateZ(rotation.z)
                .scale(scale);
    }
}

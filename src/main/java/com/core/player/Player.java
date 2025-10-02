package com.core.player;

import com.core.engine.GameObject;
import com.core.engine.components.Transform;
import com.core.graphics.Camera;
import com.core.graphics.CameraMode;
import com.core.graphics.Window;
import com.core.graphics.mesh.Mesh;
import com.core.graphics.material.Material;
import com.core.graphics.mesh.MeshFactory;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Player extends GameObject {

    private float yaw = -90.0f;
    private float pitch = 0.0f;
    private float height;

    private Vector3f velocity = new Vector3f();
    private int numberOfJumps;

    private final float gravity = 9.8f;
    private final float jumpStrength = 5.0f;

    public Player(Vector3f startPos) {
        this.transform = new Transform(startPos);
        this.transform.scale.set(0.8f, 2f, 0.8f); // paralelepípedo
        this.mesh = MeshFactory.createCube(1f, 1f, 1f);
        this.material = new Material("shaders/basic_lighting.vert", "shaders/basic_lighting.frag", null);
        this.material.setColor(0f, 1f, 0f); // verde

        this.height = 1.75f;
    }

    public Vector3f getPosition() { return transform.position; }
    public void setPosition(Vector3f pos) { transform.position.set(pos); }

    public Vector3f getVelocity() { return velocity; }
    public void setVelocity(Vector3f vel) { this.velocity.set(vel); }

    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }

    public Vector3f getEyesPosition() {
        return new Vector3f(transform.position.x,
                transform.position.y + height,
                transform.position.z);
    }

    @Override
    public void update(double dt) {
        float delta = (float) dt;

        // --- movimento horizontal ---
        transform.position.x += velocity.x * delta;
        transform.position.z += velocity.z * delta;

        // --- gravidade / pulo ---
        if (numberOfJumps > 0) {
            velocity.y -= gravity * delta;
            transform.position.y += velocity.y * delta;

            if (transform.position.y <= 0f) {
                transform.position.y = 0f;
                velocity.y = 0f;
                numberOfJumps = 0;
            }
        }
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
        material.cleanup();
    }

    public void processKeyboard(int key, double dt, float speed) {
        Vector3f forward = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)),
                0,
                (float) Math.sin(Math.toRadians(yaw))
        ).normalize();

        Vector3f right = new Vector3f(forward).cross(0,1,0).normalize();

        if (key == GLFW.GLFW_KEY_W) velocity.add(new Vector3f(forward).mul(speed));
        if (key == GLFW.GLFW_KEY_S) velocity.add(new Vector3f(forward).mul(-speed));
        if (key == GLFW.GLFW_KEY_A) velocity.add(new Vector3f(right).mul(-speed));
        if (key == GLFW.GLFW_KEY_D) velocity.add(new Vector3f(right).mul(speed));
    }

    public void jump() {
        if (numberOfJumps < 2) {
            velocity.y = jumpStrength;
            numberOfJumps++;
        }
    }
}

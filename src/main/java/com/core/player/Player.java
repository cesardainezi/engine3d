package com.core.player;

import com.core.engine.GameObject;
import com.core.engine.components.Transform;
import com.core.graphics.material.Material;
import com.core.graphics.mesh.MeshFactory;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

public class Player extends GameObject {

    // Physical attributes
    private float yaw = -90.0f;
    private float pitch = 0.0f;
    private final float height = 1.75f;

    // Movement attributes
    private Vector3f velocity = new Vector3f();
    private float accel = 15.0f;        // aceleração (unidades/s²)
    private float maxSpeed = 3.5f;      // velocidade máxima
    private float friction = 40.0f;
    private float gravity = 9.8f;

    // Special movement
    private int numberOfJumps;
    private final float jumpStrength = 4.0f;

    public Player(Vector3f startPos) {
        this.transform = new Transform(startPos);
        this.transform.scale.set(0.8f, 2.0f, 0.8f); // paralelepípedo
        this.mesh = MeshFactory.createCube(1.0f, 1.0f, 1.0f);
        this.material = new Material("shaders/basic_lighting.vert", "shaders/basic_lighting.frag", null);
        this.material.setColor(0.0f, 1.0f, 0f); // verde
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

    public void processKeyboard(Set<Integer> keys, double dt) {
        if(numberOfJumps > 0)
            return;

        // Get player's forward direction
        Vector3f forward = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)),
                0,
                (float) Math.sin(Math.toRadians(yaw))
        ).normalize();

        // Sets up direction
        Vector3f up = new Vector3f(0f, 1f, 0f);

        // Calculate right direction
        Vector3f right = new Vector3f(forward).cross(up);

        // Decides input direction
        Vector3f inputDir = new Vector3f();
        if (keys.contains(GLFW.GLFW_KEY_W)) inputDir.add(forward);
        if (keys.contains(GLFW.GLFW_KEY_S)) inputDir.sub(forward);
        if (keys.contains(GLFW.GLFW_KEY_A)) inputDir.sub(right);
        if (keys.contains(GLFW.GLFW_KEY_D)) inputDir.add(right);

        if (inputDir.lengthSquared() > 0) {
            inputDir.normalize();

            // aceleração na direção do input
            velocity.add(new Vector3f(inputDir).mul(accel * (float) dt));
        } else {
            // sem input → aplica atrito para reduzir a velocidade horizontal gradualmente
            Vector3f horizontalVel = new Vector3f(velocity.x, 0, velocity.z);

            if (horizontalVel.lengthSquared() > 0) {
                Vector3f frictionVec = new Vector3f(horizontalVel).normalize().mul(friction * (float) dt);

                if (frictionVec.lengthSquared() < horizontalVel.lengthSquared()) {
                    // Reduz a velocidade horizontal sem afetar a vertical
                    velocity.x -= frictionVec.x;
                    velocity.z -= frictionVec.z;
                } else {
                    // Para completamente a velocidade horizontal
                    velocity.x = 0;
                    velocity.z = 0;
                }
            }
        }

        // limitar velocidade máxima (só horizontal)
        Vector3f horizontalVel = new Vector3f(velocity.x, 0, velocity.z);
        if (horizontalVel.length() > maxSpeed) {
            horizontalVel.normalize().mul(maxSpeed);
            velocity.x = horizontalVel.x;
            velocity.z = horizontalVel.z;
        }
    }

    public void jump() {
        if (numberOfJumps < 2) {
            velocity.y = jumpStrength;
            numberOfJumps++;
        }
    }
}

package com.core.graphics;

import com.core.player.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

public class Camera {

    public Vector3f position;
    public Vector3f front;
    public Vector3f up;

    private Player player;
    private float yaw;
    private float pitch;

    private float speed = 5f;       // unidades por segundo
    private float sensitivity = 0.1f; // para o mouse

    private CameraMode mode;

    public void setMode(CameraMode mode) {
        this.mode = mode;
    }
    public CameraMode getMode() {
        return mode;
    }

    public Camera(Player player) {
        this.player = player;
        this.position = new Vector3f(player.getPosition());
        this.front = new Vector3f(0, 0, -1);
        this.up = new Vector3f(0, 1, 0);
        this.yaw = -90.0f; // olhando para -Z
        this.pitch = 0.0f;
        this.mode = CameraMode.PLAYER;
    }

    public Matrix4f getViewMatrix() {
        Vector3f target = new Vector3f(position).add(front);
        return new Matrix4f().lookAt(position, target, up);
    }

    public void processKeyboard(Set<Integer> keys, double dt) {
        float velocity = speed * (float) dt;

        if (mode == CameraMode.FREECAM) {
            Vector3f right = new Vector3f();
            front.cross(up, right).normalize();

            if (keys.contains(GLFW.GLFW_KEY_W))
                position.add(new Vector3f(front).mul(velocity));
            if (keys.contains(GLFW.GLFW_KEY_S))
                position.sub(new Vector3f(front).mul(velocity));
            if (keys.contains(GLFW.GLFW_KEY_A))
                position.sub(new Vector3f(right).mul(velocity));
            if (keys.contains(GLFW.GLFW_KEY_D))
                position.add(new Vector3f(right).mul(velocity));
            if (keys.contains(GLFW.GLFW_KEY_SPACE))
                position.add(new Vector3f(up).mul(velocity));
            if (keys.contains(GLFW.GLFW_KEY_LEFT_SHIFT))
                position.sub(new Vector3f(up).mul(velocity));
        }
        else if (mode == CameraMode.PLAYER) {
            // Agora passa o conjunto inteiro de teclas para o player
            player.processKeyboard(keys, dt);
        }
    }


    public void toggleMode() {
        if (mode == CameraMode.FREECAM) {
            mode = CameraMode.PLAYER;
            position.y = player.getPosition().y + 1.75f;

            if (player != null) {
                // 🔹 Sincroniza camera com orientação do player
                this.yaw = player.getYaw();
                this.pitch = player.getPitch();
                updateCameraVectors();
            }

            System.out.println("Camera mode: PLAYER");
        } else {
            mode = CameraMode.FREECAM;

            if (player != null) {
                position.y = player.getPosition().y + 2.5f;
            }

            System.out.println("Camera mode: FREE CAM");
        }
    }

    public void processMouse(float xoffset, float yoffset) {
        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;

        // 🔹 Se estivermos em PLAYER mode, atualizar o player também
        if (mode == CameraMode.PLAYER && player != null) {
            player.setYaw(yaw);
            player.setPitch(pitch);
        }

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        Vector3f newFront = new Vector3f();
        newFront.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        newFront.y = (float) Math.sin(Math.toRadians(pitch));
        newFront.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
        front = newFront.normalize();
    }
}
package com.core.engine;

import com.core.graphics.Camera;
import com.core.graphics.CameraMode;
import com.core.graphics.TextRenderer;
import com.core.graphics.Window;
import com.core.input.Input;
import com.core.models.Cube;
import com.core.models.Floor;
import com.core.player.Player;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameManager implements Runnable {

    private boolean running = false;
    private Window window;
    private Input input;
    private Scene scene;
    private TextRenderer textRenderer;
    private int fps, ups; // para armazenar contadores

    @Override
    public void run() {

        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loop();
        cleanup();
    }

    private void init() throws IOException {
        window = new Window(800, 600, "Meu Jogo LWJGL");
        window.create();

        // Initialize input
        input = new Input(window.getWindowHandle());
        input.initMouse();

        scene = new Scene(new Vector3f(0f, 0f, 2f), input);
        scene.init();

        GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        textRenderer = new TextRenderer();
        textRenderer.init("assets/fonts/arial.ttf", window);

        float defaultTime = 30f;
        textRenderer.addTimedText("F4: Toogle Free cam / Player", 10, 100, defaultTime);
        textRenderer.addTimedText("WASD: Move player / camera", 10, 120, defaultTime);
        textRenderer.addTimedText("Space: Jump / double jump", 10, 140, defaultTime);
        textRenderer.addTimedText("Left Shift: Sprint", 10, 160, defaultTime);
        textRenderer.addTimedText("Left Ctrl: Crouch", 10, 180, defaultTime);
        textRenderer.addTimedText("Left Alt: Slow walk", 10, 200, defaultTime);
        textRenderer.addTimedText("Alt+Enter or F11: Window mode", 10, 220, defaultTime);
        textRenderer.addTimedText("Esc: Quit game", 10, 240, defaultTime);
    }

    private void loop() {
        running = true;

        final double frameCap = 1.0 / 60.0;  // Taxa de render
        final double updateCap = 1.0 / 60.0; // Taxa de update

        double lastTime = System.nanoTime() / 1e9;
        double unprocessedUpdate = 0;
        double unprocessedFrame = 0;
        double currentTime, passed, lastUpdateTime, thisUpdateTime;

        int frames = 0;
        int updates = 0;
        double fpsTimer = 0;

        // Game loop
        while (running && !window.shouldClose()) {
            currentTime = System.nanoTime() / 1e9;
            passed = currentTime - lastTime;
            lastTime = currentTime;

            unprocessedUpdate += passed;
            unprocessedFrame += passed;
            fpsTimer += passed;

            // 🔹 Updates fixos
            while (unprocessedUpdate >= updateCap) {
                update(updateCap);
                updates++;
                unprocessedUpdate -= updateCap;

                // Evita "espiral da morte" se CPU travar
                if (updates > 10) break;
            }

            // 🔹 Render limitado a frameCap (60/FPS)
            if (unprocessedFrame >= frameCap) {

                // Realiza render
                render();
                frames++;
                unprocessedFrame = 0;
            }

            // 🔹 A cada 1s, mostra UPS/FPS
            if (fpsTimer >= 1.0) {
                fps = frames;
                ups = updates;
                System.out.println("UPS: " + ups + " | FPS: " + fps);

                frames = 0;
                updates = 0;
                fpsTimer = 0;
            }
        }
    }

    private void update(double dt) {
        window.pollEvents();
        input.update();
        scene.update(dt);
        textRenderer.update(dt); // atualiza fades, timers, etc.

        // Close game with ESC
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) {
            running = false;
        }

        // Fullscreen with Alt+Enter
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_ENTER) &&
                (input.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || input.isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT))) {
            window.toggleFullscreen();
        }

        // Fullscreen with F11
        if (input.isKeyJustReleased(GLFW.GLFW_KEY_F11)) window.toggleFullscreen();
    }

    private void render() {
        scene.render(window);

        // Render HUD
        textRenderer.drawText("FPS: " + fps + " | UPS: " + ups, 10, 30, 1.0f);
        textRenderer.render();

        window.swapBuffers();
    }

    private void cleanup() {
        window.destroy();
        scene.cleanup();
    }
}
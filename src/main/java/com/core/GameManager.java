package com.core;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameManager implements Runnable {

    private boolean running = false;
    private Window window;
    private Renderer renderer;
    private Input input;
    private Camera camera;
    private List<GameObject> objects = new ArrayList<>();
    private Player player;
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

        GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        player = new Player(new Vector3f(0.0f, 0.0f, 2.0f));
        player.init();

        camera = new Camera(player);

        Cube cube = new Cube(new Vector3f(0.5f, 0.866f, 0.5f));
        cube.init();
        objects.add(cube);

        Floor plane = new Floor();
        plane.init();
        objects.add(plane);

        renderer = new Renderer();
        renderer.init();

        textRenderer = new TextRenderer();
        textRenderer.init("assets/fonts/arial.ttf", window);

        input = new Input(window.getWindowHandle());
        input.initMouse();

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

        for (GameObject obj : objects) {
            obj.update(dt);
        }

        player.update(dt);

        if (camera.getMode() == CameraMode.PLAYER) {
            // zera movimento horizontal
            player.getVelocity().x = 0;
            player.getVelocity().z = 0;

            // camera segue o player
            camera.position.set(
                    player.getPosition().x,
                    player.getPosition().y + 1.75f,
                    player.getPosition().z
            );

            if (input.isKeyPressed(GLFW.GLFW_KEY_W)) camera.processKeyboard(GLFW.GLFW_KEY_W, dt);
            if (input.isKeyPressed(GLFW.GLFW_KEY_S)) camera.processKeyboard(GLFW.GLFW_KEY_S, dt);
            if (input.isKeyPressed(GLFW.GLFW_KEY_A)) camera.processKeyboard(GLFW.GLFW_KEY_A, dt);
            if (input.isKeyPressed(GLFW.GLFW_KEY_D)) camera.processKeyboard(GLFW.GLFW_KEY_D, dt);
        }

        // Exemplo: Encerra com ESC
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) {
            running = false;
        }

        if (input.isKeyJustPressed(GLFW.GLFW_KEY_ENTER) &&
                (input.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || input.isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT))) {
            window.toggleFullscreen();
        }

        if (input.isKeyJustReleased(GLFW.GLFW_KEY_F11)) window.toggleFullscreen();
        if (input.isKeyJustReleased(GLFW.GLFW_KEY_F4)) camera.toggleMode();

        if (input.isKeyPressed(GLFW.GLFW_KEY_W)) camera.processKeyboard(GLFW.GLFW_KEY_W, dt);
        if (input.isKeyPressed(GLFW.GLFW_KEY_S)) camera.processKeyboard(GLFW.GLFW_KEY_S, dt);
        if (input.isKeyPressed(GLFW.GLFW_KEY_A)) camera.processKeyboard(GLFW.GLFW_KEY_A, dt);
        if (input.isKeyPressed(GLFW.GLFW_KEY_D)) camera.processKeyboard(GLFW.GLFW_KEY_D, dt);
        if (input.isKeyJustPressed(GLFW.GLFW_KEY_SPACE)) player.jump();

        double dx = input.getDeltaX();
        double dy = input.getDeltaY();

        if (dx != 0 || dy != 0) {
            camera.processMouse((float) dx, (float) dy);
        }
    }

    private void render() {
        renderer.clear();

        for (GameObject obj : objects) {
            obj.render(camera, window);
        }

        if (camera.getMode() == CameraMode.FREECAM) {
            player.render(camera, window);
        }

        // Render HUD
        textRenderer.drawText("FPS: " + fps + " | UPS: " + ups, 10, 30);

        window.swapBuffers();
    }

    private void cleanup() {
        window.destroy();
    }
}
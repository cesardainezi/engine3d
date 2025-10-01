package com.core.engine;

import com.core.graphics.Camera;
import com.core.graphics.CameraMode;
import com.core.graphics.Window;
import com.core.input.Input;
import com.core.models.Cube;
import com.core.models.Floor;
import com.core.player.Player;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private Vector3f spawnPoint;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<GameObject> objects;
    private Input input;

    public Scene(Vector3f spawnPoint, Input input){
        this.spawnPoint = spawnPoint;
        this.input = input;
    }

    public void init(){
        // Initialize player
        player = new Player(spawnPoint);
        player.init();

        // Initialize object list
        objects = new ArrayList<>();

        // Initialize scene renderer
        renderer = new Renderer();
        renderer.init();
        renderer.setBackgroundColor(0f, 0.25f, 0.5f);

        // Initialize camera
        camera = new Camera(player);

        // Add objects
        Cube cube = new Cube(new Vector3f(0.5f, 0.866f, 0.5f), 1.414f);
        objects.add(cube);

        cube = new Cube(new Vector3f(-1.5f, 0.866f, 0.5f), 2f);
        objects.add(cube);

        Floor plane = new Floor();
        objects.add(plane);
    }

    public void update(double dt){
        // Updates all scene objects
        for (GameObject obj : objects) {
            obj.update(dt);
        }

        // Updates player
        player.update(dt);

        // Handles camera modes
        if (input.isKeyJustReleased(GLFW.GLFW_KEY_F4)) camera.toggleMode();

        // If 1st person (PLAYER mode)
        if (camera.getMode() == CameraMode.PLAYER) {
            // Nullify horizontal movement
            player.getVelocity().x = 0;
            player.getVelocity().z = 0;

            // Camera follows player position + height

            camera.position.set(player.getEyesPosition());

            if (input.isKeyPressed(GLFW.GLFW_KEY_W)) camera.processKeyboard(GLFW.GLFW_KEY_W, dt);
            if (input.isKeyPressed(GLFW.GLFW_KEY_S)) camera.processKeyboard(GLFW.GLFW_KEY_S, dt);
            if (input.isKeyPressed(GLFW.GLFW_KEY_A)) camera.processKeyboard(GLFW.GLFW_KEY_A, dt);
            if (input.isKeyPressed(GLFW.GLFW_KEY_D)) camera.processKeyboard(GLFW.GLFW_KEY_D, dt);
        }

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

    public void render(Window window){
        renderer.clear();

        renderer.renderScene(objects, camera, window);

        if (camera.getMode() == CameraMode.FREECAM) {
            player.render(camera, window);
        }
    }

    public void cleanup(){
        for(GameObject obj : objects){
            obj.cleanup();
        }
    }
}

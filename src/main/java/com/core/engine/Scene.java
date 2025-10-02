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
import java.util.Set;

public class Scene {

    private Vector3f spawnPoint;
    private Player player;
    private Renderer renderer;
    private Camera camera;
    private List<GameObject> objects;
    private Input input;
    private final float gravity = 9.8f;

    public Scene(Vector3f spawnPoint, Input input){
        this.spawnPoint = spawnPoint;
        this.input = input;
    }

    public void init(){
        // Initialize player
        player = new Player(spawnPoint);

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

    public void update(double dt) {
        // Updates all scene objects
        for (GameObject obj : objects) {
            obj.update(dt);
        }

        // Updates player
        player.update(dt);

        // Handles camera modes
        if (input.isKeyJustReleased(GLFW.GLFW_KEY_F4)) camera.toggleMode();

        // Pega todas as teclas pressionadas neste frame
        Set<Integer> pressedKeys = input.getPressedKeys();

        // Se estiver no modo PLAYER → câmera segue player
        if (camera.getMode() == CameraMode.PLAYER) {
            // Câmera segue a cabeça do player
            camera.position.set(player.getEyesPosition());

            if (pressedKeys.contains(GLFW.GLFW_KEY_SPACE)) {
                player.jump();
            }
        }

        // Passa todas as teclas para a câmera (ela decide se aplica ao freecam ou ao player)
        camera.processKeyboard(pressedKeys, dt);

        // Mouse look
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
            renderer.renderObject(player, camera, window);
        }
    }

    public void cleanup(){
        for(GameObject obj : objects){
            obj.cleanup();
        }
    }
}

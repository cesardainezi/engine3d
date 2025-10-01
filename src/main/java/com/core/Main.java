package com.core;

import com.core.engine.GameManager;

public class Main {
    public static void main(String[] args) {
        GameManager game = new GameManager();
        game.run(); // pode rodar direto
        // ou em thread: new Thread(game).start();
    }
}

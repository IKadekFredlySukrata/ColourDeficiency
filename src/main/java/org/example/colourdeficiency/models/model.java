package org.example.colourdeficiency.models;

import javafx.scene.Scene;

public class model {
    static Scene scene;
    public static Scene getScene(){
        return scene;
    }
    public static void UpdateScene(Scene newscene){
        scene = newscene;
    }
}

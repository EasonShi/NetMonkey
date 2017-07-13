package com.stone.netmonkey;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/index.fxml"));
        primaryStage.setTitle("网猴");
        primaryStage.setScene(new Scene(root));
        primaryStage.setHeight(500);primaryStage.setWidth(700);
        primaryStage.setMaxHeight(500);primaryStage.setMaxWidth(700);
        primaryStage.setMinHeight(500);primaryStage.setMinWidth(700);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

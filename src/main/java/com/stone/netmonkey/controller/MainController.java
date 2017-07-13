package com.stone.netmonkey.controller;

import com.stone.netmonkey.NetService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void startTask(ActionEvent actionEvent) {
        NetService.start("http://isujin.com",1,null);
    }

    public void exitApp(ActionEvent actionEvent) {
        System.exit(0);
    }
}

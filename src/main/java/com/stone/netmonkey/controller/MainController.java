package com.stone.netmonkey.controller;

import com.stone.netmonkey.service.AppInfo;
import com.stone.netmonkey.service.NetService;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Appinfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Button startBtn;  //开始按钮

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void startTask(ActionEvent actionEvent) {
        NetService.start("http://isujin.com/",1,null,"苏瑾");
    }

    public void exitApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void createProject(ActionEvent actionEvent) {
        TextInputDialog projectNameDialog = new TextInputDialog("");
        projectNameDialog.setTitle("新建项目");
        projectNameDialog.setHeaderText("填写项目名");
        projectNameDialog.setContentText("项目名：");

// Traditional way to get the response value.
        Optional<String> result = projectNameDialog.showAndWait();
        result.ifPresent(name -> {
            if(name.trim().isEmpty()){
                new Alert(Alert.AlertType.ERROR,"请填写项目名").show();
            }else{
                TextInputDialog indexUrlDialog = new TextInputDialog("");
                indexUrlDialog.setTitle("新建项目");
                indexUrlDialog.setHeaderText("填写URL");
                indexUrlDialog.setContentText("URL：");
                Optional<String> urlResult = indexUrlDialog.showAndWait();
//                urlResult.isPresent(name -> {});
                AppInfo.nowProjectName = name;
                startBtn.setDisable(false);
            }
        });
    }
}

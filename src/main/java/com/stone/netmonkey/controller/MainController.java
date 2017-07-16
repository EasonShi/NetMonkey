package com.stone.netmonkey.controller;

import com.stone.netmonkey.service.AppInfo;
import com.stone.netmonkey.service.DownLoadService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Button startBtn;  //开始按钮
    public Label projectState;
    public TableView infoTableView;
    public TextField tierTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList columns = infoTableView.getColumns();
        TableColumn c_name = (TableColumn) columns.get(0);
        TableColumn c_url = (TableColumn) columns.get(1);
        TableColumn c_state = (TableColumn) columns.get(2);
        c_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        c_url.setCellValueFactory(new PropertyValueFactory<>("url"));
        c_state.setCellValueFactory(new PropertyValueFactory<>("state"));
    }

    public void startTask(ActionEvent actionEvent) {
        DownLoadService downLoadService = new DownLoadService();
        downLoadService.setOnSucceeded(event -> {
            //任务成功
            new Alert(Alert.AlertType.INFORMATION,"任务下载成功").show();
        });
        downLoadService.setMainController(this);
        downLoadService.start();


    }

    public void exitApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * 新建项目按钮
     * @param actionEvent
     */
    public void createProject(ActionEvent actionEvent) {
        TextInputDialog projectNameDialog = new TextInputDialog("");
        projectNameDialog.setTitle("新建项目");
        projectNameDialog.setHeaderText("填写项目名");
        projectNameDialog.setContentText("项目名：");
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
                if(urlResult.isPresent()){
                    String url = urlResult.get();
                    if(url.trim().isEmpty()){
                        new Alert(Alert.AlertType.ERROR,"请填写URL").show();
                    }else if(url.trim().length()<10){
                        new Alert(Alert.AlertType.ERROR,"URL不正确").show();
                    }else if(!url.trim().substring(0,4).equals("http")){
                        new Alert(Alert.AlertType.ERROR,"URL缺少http(https)").show();
                    }else{
                        AppInfo.nowUrl = url;
                        AppInfo.nowProjectName = name;
                        startBtn.setDisable(false);
                        projectState.setText("当前:"+AppInfo.nowProjectName);
                    }
                }
            }
        });
    }

    public void test(ActionEvent actionEvent) {
        System.out.println(1);
    }
}

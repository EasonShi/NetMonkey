package com.stone.netmonkey.service;


import com.stone.netmonkey.controller.MainController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * Created by eason on 17-7-14.
 */
public class DownLoadService extends Service {

    private MainController mainController;

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                new NetService(mainController).start(AppInfo.nowUrl, Integer.parseInt(mainController.tierTextField.getText()),null,AppInfo.nowProjectName);
                return true;
            }
        };
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}

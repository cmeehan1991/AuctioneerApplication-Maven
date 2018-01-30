/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.items;

import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.WindowController;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class ItemMain extends Application {

    public Group group;
    public boolean isNew;
    public String itemNumber;
    ViewItemsFXMLController itemsController;
    public FXDesktopPane desktopPane = MainApp.desktopPane;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ItemFXML.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        ItemFXMLController controller = (ItemFXMLController) loader.getController();
        String title = "New Item";
        if (!isNew) {
            title = "Item " + itemNumber;
            controller.getItem(itemNumber);
        }

        FXInternalWindow fxInternalWindow = new WindowController().newInternalWindow(title, root);
        desktopPane.getWindows().add(fxInternalWindow);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bidder;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.Window;

/**
 *
 * @author cmeehan
 */
public class BidderListMain extends Application {

    public Group group;

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BidderListFXML.fxml"));
        try {
            Parent root = (Parent) loader.load();

            BidderListFXMLController controller = (BidderListFXMLController) loader.getController();
            controller.group = group;
            Window window = new Window("Bidders");
            window.setBoundsListenerEnabled(true);
            window.setLayoutX(10);
            window.setLayoutY(100);
            window.setPrefHeight(500);
            window.setPrefWidth(900);

            window.getRightIcons().add(new CloseIcon(window));

            window.getContentPane().getChildren().add(root);

            group.getChildren().add(window);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bidder;

import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.WindowController;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class BidderMain extends Application {

    public String bidderId;
    public boolean isNew;
    public FXDesktopPane desktopPane = MainApp.desktopPane;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BidderFXML.fxml"));
        AnchorPane anchorPane = (AnchorPane) loader.load();
        BidderFXMLController controller = (BidderFXMLController) loader.getController();
        
        if(!isNew) {
        		controller.getBidder(bidderId);
        }
        FXInternalWindow internalWindow = new WindowController().newInternalWindow("Auction Dashboard", anchorPane);
		desktopPane.getWindows().add(internalWindow);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

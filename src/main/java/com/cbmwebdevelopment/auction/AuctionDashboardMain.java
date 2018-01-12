/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

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
public class AuctionDashboardMain extends Application {
    public FXDesktopPane desktopPane;
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuctionDashboardFXML.fxml"));
        AnchorPane rootPane = (AnchorPane) loader.load();
        AuctionDashboardFXMLController controller = loader.getController();
        
        FXInternalWindow internalWindow = new WindowController().newInternalWindow("Auction Dashboard", rootPane);
        desktopPane.getWindows().add(internalWindow);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

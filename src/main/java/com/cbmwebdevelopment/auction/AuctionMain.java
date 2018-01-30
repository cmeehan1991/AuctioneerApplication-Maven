package com.cbmwebdevelopment.auction;
import java.io.IOException;

import com.cbmwebdevelopment.main.MainApp;
import com.cbmwebdevelopment.main.WindowController;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXDesktopPane;
import com.sibvisions.rad.ui.javafx.ext.mdi.FXInternalWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AuctionMain extends Application{
	public FXDesktopPane desktopPane = MainApp.desktopPane;
	public String auctionId;
	@Override 
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuctionFXML.fxml"));
		AnchorPane anchorPane = (AnchorPane) loader.load();
		AuctionFXMLController controller = (AuctionFXMLController) loader.getController();
		if(auctionId != null) { // If an ID value was passed to this class then we are working with an existing auction.
			controller.isNew = false;
			controller.auctionId = auctionId;
			controller.getAuction(auctionId);
		}
		
		FXInternalWindow internalWindow = new WindowController().newInternalWindow("Auction Dashboard", anchorPane);
		desktopPane.getWindows().add(internalWindow);
	}
	
	public static void main(String...args) {
		launch(args);
	}

}

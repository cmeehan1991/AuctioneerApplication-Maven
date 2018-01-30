/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.items;

import com.cbmwebdevelopment.auction.Auction;
import com.cbmwebdevelopment.main.Values;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PrefixSelectionComboBox;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ItemFXMLController implements Initializable {

    @FXML
    TextField itemNumberTextField, itemNameTextField, itemDescriptionTextField, minimumBidTextField, auctionSearchField;

    @FXML
    PrefixSelectionComboBox<String> itemTypeComboBox;

    @FXML
    ListView<String> auctionListView;

    @FXML
    ToggleGroup itemAuctionTypeGroup;

    @FXML
    ImageView itemImageImageView;

    @FXML
    RadioButton silentAuctionRadioButton, liveAuctionRadioButton;

    @FXML
    Label itemClosedLabel;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    GridPane mainContent;

    protected String itemNumber, itemName, itemDescription, minimumBid;

    protected boolean silentAuction, liveAuction;

    protected File itemImage;

    private List<String> missingItems;
    protected ObservableList<Node> children;
    public static ObservableList<String> auctionIds = FXCollections.observableArrayList();

    @FXML
    protected void addItemImage(ActionEvent event) throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        itemImage = fileChooser.showOpenDialog(new Stage());

        Image image = new Image(itemImage.toURI().toURL().toString());
        System.out.println(itemImage.toURI().toURL().toString());
        itemImageImageView.setImage(image);
        itemImageImageView.setFitWidth(100);
        itemImageImageView.setFitHeight(100);
        itemImageImageView.setPreserveRatio(true);

    }

    @FXML
    protected void saveItem(ActionEvent event) throws IOException {
        if (validateFields()) {
            Item item = new Item();
            item.saveItem(this);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Items");
            alert.setHeaderText("Several required items are missing");
            alert.setContentText("Please correct the following missing items");

            // Expandable Exception
            TextArea textArea = new TextArea();
            missingItems.forEach((item) -> {
                textArea.appendText(item + "\n");
            });
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);

            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(true);

            alert.showAndWait();
        }
    }

    private boolean validateFields() {
        boolean isValid = false;
        missingItems = new ArrayList<>();
        assignValues();

        if (itemName.trim().isEmpty() || itemName == null) {
            missingItems.add("Item Name");
        }

        if (liveAuction == false && silentAuction == false) {
            missingItems.add("Auction Type");
        }

        if (missingItems.isEmpty() || missingItems == null) {
            isValid = true;
        }

        return isValid;
    }

    private void assignValues() {
        itemNumber = itemNumberTextField.getText();
        itemName = itemNameTextField.getText();
        itemDescription = itemDescriptionTextField.getText();
        silentAuction = silentAuctionRadioButton.isSelected();
        liveAuction = liveAuctionRadioButton.isSelected();
        minimumBid = minimumBidTextField.getText();
    }

    protected void getItem(String itemNumber) {
        progressIndicator.setVisible(true);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            HashMap<String, String> item = new Item().getValues(itemNumber);
            Platform.runLater(() -> {
                if (item == null || item.isEmpty()) {
                    Notifications.create().text("Item does not exist").position(Pos.BOTTOM_RIGHT).showWarning();
                    progressIndicator.setVisible(false);
                } else {
                    itemNumberTextField.setText(itemNumber);
                    itemNameTextField.setText(item.get("name"));
                    itemDescriptionTextField.setText(item.get("description"));
                    minimumBidTextField.setText(item.get("reserve"));
                    silentAuctionRadioButton.setSelected(Boolean.parseBoolean(item.get("silentAuction")));
                    liveAuctionRadioButton.setSelected(Boolean.parseBoolean(item.get("liveAuction")));
                    itemClosedLabel.setVisible(Boolean.parseBoolean(item.get("closed")));
                    itemTypeComboBox.getSelectionModel().select("N/A");
                    progressIndicator.setVisible(false);
                    System.out.println(item.get("auction"));
                    auctionListView.getSelectionModel().select(item.get("auction"));
                }
            });
            executor.shutdown();
        });
    }

    private void getAuction(String text){
    	System.out.println("Getting auctions");
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(()->{
	        	ObservableList<String> auctionNames = new Auction().getAuctionList(text);
	        	Platform.runLater(()->{
	        		auctionListView.getItems().setAll(auctionNames);
	        	});
	        	executor.shutdown();
        });
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        progressIndicator.setVisible(false);
        itemClosedLabel.setVisible(false); // Setting this label to invisible by default.
        itemTypeComboBox.getItems().setAll(Values.AUCTION_ITEM_CATEGORIES);
        itemTypeComboBox.getSelectionModel().select(0);
        
        // Get all auctions
        getAuction(null);

        
        auctionSearchField.textProperty().addListener((obs, ov, nv)->{
        		getAuction(nv);
        });
    }

}

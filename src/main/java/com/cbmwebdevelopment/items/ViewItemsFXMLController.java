/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.items;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController.AllItems;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ViewItemsFXMLController implements Initializable {

    protected Group group;
    @FXML
    TextField searchTextField;

    @FXML
    TableView itemsTableView;

    private ItemsTableController tableController;

    @FXML
    protected void addNewItem(ActionEvent event) throws IOException {
        ItemMain itemMain = new ItemMain();
        itemMain.isNew = true;
        itemMain.group = group;
        itemMain.itemsController = this;
        itemMain.start(new Stage());
    }

    @FXML
    protected void viewSelectedItems(ActionEvent event) {
        System.out.println("View Selected");
        ObservableList<AllItems> selectedItems = itemsTableView.getSelectionModel().getSelectedItems();
        selectedItems.forEach(item -> {
            try {
                ItemMain itemMain = new ItemMain();
                itemMain.group = group;
                itemMain.itemNumber = String.valueOf(item.getId());
                itemMain.isNew = false;
                itemMain.start(new Stage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    @FXML
    protected void deleteSelectedItems(ActionEvent event) {
        ObservableList<AllItems> selectedItems = itemsTableView.getSelectionModel().getSelectedItems();
        selectedItems.forEach((selectedItem) -> {
            // Remove the item from the database
            Item item = new Item();
            item.removeItem(selectedItem.getId());
            
            // Remove the item from the table
            itemsTableView.getItems().remove(selectedItem);
        });
    }

    @FXML
    protected void searchForItem(ActionEvent event) {
        String terms = searchTextField.getText();
        if (terms != null && !terms.trim().isEmpty()) {
            populateTable(terms);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Search Terms");
            alert.setContentText("Please fill in what you would like to search for.");
            alert.showAndWait();
        }
    }

    protected void populateTable(String terms) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> {
            Platform.runLater(() -> {
                itemsTableView.getItems().setAll(new Item().getAuctionItems(terms, null));
            });
            executor.shutdown();
        });
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableController = new ItemsTableController();
        tableController.itemsTable(itemsTableView);
        populateTable(null);

        searchTextField.textProperty().addListener((obs, ov, nv) -> {
            if (nv.trim().isEmpty()) {
                populateTable(null);
            }
        });
    }

}

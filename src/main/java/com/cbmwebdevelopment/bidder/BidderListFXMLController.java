/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.bidder;

import com.cbmwebdevelopment.tablecontrollers.BiddersTableController;
import com.cbmwebdevelopment.tablecontrollers.BiddersTableController.Bidders;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class BidderListFXMLController implements Initializable {

    Group group;
    @FXML
    TextField searchInput;

    @FXML
    TableView biddersTableView;

    @FXML
    ProgressIndicator progressIndicator;

    private BiddersTableController btc;

    @FXML
    protected void search(ActionEvent event) {
        if (searchInput != null && !searchInput.getText().trim().isEmpty()) {
            getBidders(searchInput.getText());
        } else {
            System.out.println("Showing notification");
            NotificationPane np = new NotificationPane();
            np.setText("Please enter something to search for.");
            np.show();
        }
    }

    @FXML
    protected void viewBidder(ActionEvent event) {
        BidderMain bidderMain = new BidderMain();

        ObservableList<Bidders> bidders = biddersTableView.getSelectionModel().getSelectedItems();
        bidders.forEach((bidder) -> {
            bidderMain.isNew = false;
            bidderMain.group = group;
            bidderMain.bidderId = String.valueOf(bidder.getId());
            try {
                bidderMain.start(new Stage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    private void getBidders(String terms) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            //ObservableList<Bidders> data = new Bidder().getBidders(terms);
            Platform.runLater(() -> progressIndicator.toFront());
            Platform.runLater(() -> {
                biddersTableView.getItems().setAll(new Bidder().getBidders(terms));
            });
            Platform.runLater(() -> progressIndicator.setVisible(false));
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
        btc = new BiddersTableController();
        btc.biddersTable(biddersTableView);
        // Get all of the bidders
        getBidders(null);

        // Listen for the text to change on the search input
        searchInput.textProperty().addListener((obs, ov, nv) -> {
            if (nv == null || nv.trim().isEmpty()) {
                getBidders(null);
            }
        });

        // Set click listener for table
        biddersTableView.setRowFactory(tv -> {
            TableRow<Bidders> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    BidderMain bidderMain = new BidderMain();
                    bidderMain.isNew = false;
                    bidderMain.group = group;
                    bidderMain.bidderId = String.valueOf(row.getItem().getId());
                    try {
                        bidderMain.start(new Stage());
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
            });
            return row;
        });
    }

}

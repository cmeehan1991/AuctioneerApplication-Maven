/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import static com.cbmwebdevelopment.main.Values.AUCTION_TYPES;
import static com.cbmwebdevelopment.main.Values.COUNTRIES;
import static com.cbmwebdevelopment.main.Values.STATES;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController.AuctionItem;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class AuctionFXMLController implements Initializable {
	@FXML
	TextField auctionIdTextField, auctionTitleTextField, buildingNameTextField, primaryAddressTextField,
			secondaryAddressTextField, cityTextField, postalCodeTextField;

	@FXML
	TextArea auctionDescriptionTextArea;

	@FXML
	PrefixSelectionComboBox<String> auctionTypeComboBox, stateComboBox, countryComboBox;

	@FXML
	CheckBox publicCheckBox;

	@FXML
	DatePicker auctionDatePicker;

	@FXML
	TableView<AuctionItem> auctionItemsTableView;
	
	@FXML
	ProgressIndicator progressIndicator;

	protected String auctionId, title, description, type, dateTime, buildingName, primaryAddress, secondaryAddress, city,
			postalCode, state, country;
	protected boolean isPublic;
	private ArrayList<String> missingItems;
	public boolean isNew;
	private AuctionItemsTableViewController auctionItemsTableViewController;

	@FXML
	protected void saveAuctionAction(ActionEvent event) {
		if (validateValues()) {
			saveAuction();
		} else {
			
		}
	}
	private boolean validateValues() {
		missingItems = new ArrayList<>();
		assignValues();

		if (title == null || title.trim().isEmpty()) {
			missingItems.add("Auction Title");
		}

		return missingItems.isEmpty();
	}

	private void assignValues() {
		this.auctionId = auctionIdTextField.getText().trim().isEmpty() ? null : auctionIdTextField.getText();
		this.title = auctionTitleTextField.getText();
		this.description = auctionDescriptionTextArea.getText();
		this.type = auctionTypeComboBox.getSelectionModel().getSelectedItem();
		this.dateTime = auctionDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		this.buildingName = buildingNameTextField.getText();
		this.primaryAddress = primaryAddressTextField.getText();
		this.secondaryAddress = secondaryAddressTextField.getText();
		this.city = cityTextField.getText();
		this.state = stateComboBox.getSelectionModel().getSelectedIndex() < 0 ? null : stateComboBox.getSelectionModel().getSelectedItem().toString();
		this.country = countryComboBox.getSelectionModel().getSelectedItem().toString();
		this.isPublic = publicCheckBox.isSelected();
	}

	/**
	 * Use threading to either save a new auction or update and existing auction. 
	 */
	private void saveAuction() {
		progressIndicator.setVisible(true);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(()->{
			if(auctionId == null) {
				auctionId = new Auction().saveNewAuction(this);
				isNew = false;
				if(auctionId != null) {
					Platform.runLater(()->{
						Notifications.create().text("Auction ID: " + auctionId + "\nStatus: Created").showInformation();
						auctionIdTextField.setText(auctionId);
						progressIndicator.setProgress(0);
						progressIndicator.setVisible(false);
					});
				}
			}else {
				boolean updated = new Auction().saveExistingAuction(this);
				if(updated) {
					Platform.runLater(()->{
						Notifications.create().text("Auction ID: " + auctionId + "\nStatus: Updated").showInformation();
						progressIndicator.setVisible(false);
						progressIndicator.setProgress(0);
					});
				}
			}
			executor.shutdown();
		});
	}

	public void getAuction(String auctionId) {
		progressIndicator.setVisible(true);	
		// Use and executor to get the auction information
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(()->{
			HashMap<String, Object> auction = new Auction().getAuction(auctionId);
			if(auction != null) {
				Platform.runLater(()->{
					auctionIdTextField.setText(String.valueOf(auction.get("ID")));
					auctionTitleTextField.setText(String.valueOf(auction.get("NAME")));
					auctionDescriptionTextArea.setText(String.valueOf(auction.get("DESCRIPTION")));
					primaryAddressTextField.setText(String.valueOf(auction.get("PRIMARY_ADDRESS")));
					secondaryAddressTextField.setText(String.valueOf(auction.get("SECONDARY_ADDRESS")));
					cityTextField.setText(String.valueOf(auction.get("CITY")));
					stateComboBox.getSelectionModel().select(String.valueOf(auction.get("STATE")));
					postalCodeTextField.setText(String.valueOf(auction.get("POSTAL_CODE")));
					countryComboBox.getSelectionModel().select(String.valueOf(auction.get("COUNTRY")));
					buildingNameTextField.setText(String.valueOf(auction.get("BUILDING_NAME")));
					publicCheckBox.setSelected(Boolean.parseBoolean(auction.get("PUBLIC").toString()));
					auctionTypeComboBox.getSelectionModel().select(capitalizeFirstLetter(String.valueOf(auction.get("TYPE"))));
					auctionDatePicker.setValue(LocalDate.parse(String.valueOf(auction.get("AUCTION_DATE"))));
					ObservableList<AuctionItem> data = new Auction().getAuctionItems(String.valueOf(auction.get("ID")));
					auctionItemsTableView.getItems().addAll(data);
					progressIndicator.setVisible(false);
				});
			}else {
				Platform.runLater(()->{
					progressIndicator.setVisible(false);
					Notifications.create().text("Could not find the selected auction. Please try again").showError();
				});
			}
			executor.shutdown();
		});
	}
	
	/**
	 * Returns a string with the first letter being capitalized. 
	 * Ex.: live -> Live
	 * @param val
	 * @return
	 */
	private String capitalizeFirstLetter(String val) {
		StringBuilder sb = new StringBuilder();
		sb.append(val.substring(0, 1).toUpperCase());
		sb.append(val.substring(1).toLowerCase());
		return sb.toString();
	}
	/**
	 * Initializes the controller class.
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Hide the progress indicator on load
		progressIndicator.setVisible(false);

		// Assign combo box values
		auctionTypeComboBox.getItems().setAll(AUCTION_TYPES);
		auctionTypeComboBox.getSelectionModel().select(0);
		stateComboBox.getItems().setAll(STATES);
		countryComboBox.getItems().setAll(COUNTRIES);
		countryComboBox.getSelectionModel().select("United States of America");
		countryComboBox.selectionModelProperty().addListener((obs, ov, nv) -> {
					if (!nv.equals("United States of America")) {
						stateComboBox.getSelectionModel().select(-1);
						stateComboBox.setDisable(true);
					} else {
						stateComboBox.setDisable(false);
						stateComboBox.getSelectionModel().select(0);
					}
				});

		// Date and time picker default values to current date and time
		auctionDatePicker.setValue(LocalDate.now());

		// Tableview controller
		auctionItemsTableViewController = new AuctionItemsTableViewController();
		auctionItemsTableViewController.tableController(auctionItemsTableView);
		// getAuctionItems();
	}

}

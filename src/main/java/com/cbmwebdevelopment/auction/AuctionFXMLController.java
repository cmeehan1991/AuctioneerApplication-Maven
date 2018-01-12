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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jfxtras.scene.control.CalendarTimeTextField;
import org.controlsfx.control.PrefixSelectionComboBox;

import com.cbmwebdevelopment.main.Values;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
@SuppressWarnings("restriction")
public class AuctionFXMLController implements Initializable {
    @FXML
    TextField auctionIdTextField, auctionTitleTextField, buildingNameTextField, primaryAddressTextField, secondaryAddressTextField, cityTextField, postalCodeTextField;
    
    @FXML
    TextArea auctionDescriptionTextArea;
    
    @FXML
    PrefixSelectionComboBox<String> auctionTypeComboBox, stateComboBox, countryComboBox;
    
    @FXML
    CheckBox publicCheckBox;
    
    @FXML
    DatePicker auctionDatePicker;
    
    @FXML
    CalendarTimeTextField calendarTimeTextField;
    
    @FXML
    TableView<?> auctionItemsTableView;
    
    private String auctionId, title, description, type, dateTime, buildingName, primaryAddress, secondaryAddress, city, postalCode, state, country;
    private boolean isPublic;
    private ArrayList<String> missingItems;
    public boolean isNew;
    private AuctionItemsTableViewController auctionItemsTableViewController;
    
    private boolean validateValues(){
        missingItems = new ArrayList<>();
        assignValues();
        
        if(title == null || title.trim().isEmpty()){
            missingItems.add("Auction Title");
        }
        
        return missingItems.isEmpty();
    }
    
    private void assignValues(){
    		this.auctionId = auctionIdTextField.getText().trim().isEmpty() ? null : auctionIdTextField.getText();
        this.title = auctionTitleTextField.getText();
        this.description = auctionDescriptionTextArea.getText();
        this.type = auctionTypeComboBox.getSelectionModel().getSelectedItem();
        String date = Values.DATE_FORMAT.format(auctionDatePicker.getValue());
        String auctionTime = calendarTimeTextField.getAccessibleText();
        this.dateTime = date + " " + auctionTime;
        this.buildingName = 
        
    }
    
    private void getAuctionItems(){
    		
    }
    
    /**
     * Initializes the controller class.
     */
    @SuppressWarnings("unlikely-arg-type")
	@Override
    public void initialize(URL url, ResourceBundle rb) {
       // Assign combo box values
       auctionTypeComboBox.getItems().setAll(AUCTION_TYPES);
       auctionTypeComboBox.getSelectionModel().select(0);
       stateComboBox.getItems().setAll(STATES);
       countryComboBox.getItems().setAll(COUNTRIES);
       countryComboBox.getSelectionModel().select("United States of America");
       countryComboBox.selectionModelProperty().addListener((obs, ov, nv)->{
           if(!nv.equals("United States of America")){
               stateComboBox.getSelectionModel().select(-1);
               stateComboBox.setDisable(true);
           }else{
               stateComboBox.setDisable(false);
               stateComboBox.getSelectionModel().select(0);
           }
       });
       
       
       // Date and time picker default values to current date and time
       auctionDatePicker.setValue(LocalDate.now());
       calendarTimeTextField.setCalendar(Calendar.getInstance());
       
       // Tableview controller
       auctionItemsTableViewController.tableController(auctionItemsTableView);
       getAuctionItems();
    }    
    
}

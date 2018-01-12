/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.tablecontrollers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class AuctionItemsTableViewController {
    private ObservableList<AuctionItems> data = FXCollections.observableArrayList();
    public void tableController(TableView tableView){
        TableColumn<AuctionItems, Integer> idColumn = new TableColumn("ID");
        TableColumn<AuctionItems, String> nameColumn = new TableColumn("Item Name");
        TableColumn<AuctionItems, String> descriptionColumn = new TableColumn("Description");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.33));
        descriptionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.67));
        
        tableView.getColumns().setAll(idColumn, nameColumn, descriptionColumn);
        tableView.getItems().setAll(data);
        tableView.setEditable(false);
    }
    
    public static class AuctionItems{
        private final SimpleStringProperty name, description;
        private final SimpleIntegerProperty id;
        
        public AuctionItems(Integer id, String name, String description){
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(description);
        }
        
        public void setId(Integer val){
            id.set(val);
        }
        
        public Integer getId(Integer val){
            return id.getValue();
        }
    }
}

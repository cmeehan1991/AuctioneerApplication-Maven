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
    private ObservableList<AuctionItem> data = FXCollections.observableArrayList();
    @SuppressWarnings("unchecked")
	public void tableController(TableView<AuctionItem> auctionItemsTableView){
        TableColumn<AuctionItem, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<AuctionItem, String> nameColumn = new TableColumn<>("Item Name");
        TableColumn<AuctionItem, String> descriptionColumn = new TableColumn<>("Description");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        idColumn.prefWidthProperty().bind(auctionItemsTableView.widthProperty().multiply(0.10));
        nameColumn.prefWidthProperty().bind(auctionItemsTableView.widthProperty().multiply(0.33));
        descriptionColumn.prefWidthProperty().bind(auctionItemsTableView.widthProperty().multiply(0.67));
        
        auctionItemsTableView.getColumns().setAll(idColumn, nameColumn, descriptionColumn);
        auctionItemsTableView.getItems().setAll(data);
        auctionItemsTableView.setEditable(false);
    }
    
    public static class AuctionItem{
        private final SimpleStringProperty name, description;
        private final SimpleIntegerProperty id;
        
        public AuctionItem(Integer id, String name, String description){
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
        
        public void setName(String val){
        		name.set(val);
        }
        
        public String getName(){
            return name.getValue();
        }
        
        public void setDescription(String val){
        	description.set(val);
        }
        
        public String getDescription(){
            return description.getValue();
        }
    }
}

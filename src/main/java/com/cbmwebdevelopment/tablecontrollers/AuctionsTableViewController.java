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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class AuctionsTableViewController {
    public ObservableList<Auctions> data = FXCollections.observableArrayList();
    
    public void tableView(TableView tableView){
        TableColumn<Auctions, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Auctions, String> titleColumn = new TableColumn<>("Auction");
        TableColumn<Auctions, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Auctions, String> typeColumn = new TableColumn<>("Type");
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        titleColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        typeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        
        tableView.getColumns().setAll(idColumn, titleColumn, dateColumn, typeColumn);
        tableView.getItems().setAll(data);
        tableView.setEditable(false);
        
        // Row click listener
        tableView.setRowFactory(tv->{
            TableRow<Auctions> row = new TableRow<>();
            row.setOnMouseClicked((event)->{
                if(event.getClickCount() == 2 && (!row.isEmpty())){}
                Auctions data = row.getItem();
                
            });
            return row;
        });
    }
    
    public static class Auctions{
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty title, date, type;
        
        public Auctions(Integer id, String title, String date, String type){
            this.id = new SimpleIntegerProperty(id);
            this.title = new SimpleStringProperty(title);
            this.date = new SimpleStringProperty(date);
            this.type = new SimpleStringProperty(type);
        }
        
        public void setId(Integer val){
            id.set(val);
        }
        
        public Integer getId(){
            return id.getValue();
        }
        
        public void setTitle(String val){
            title.set(val);
        }
        
        public String getTitle(){
            return title.get();
        }
        
        public void setDate(String val){
            date.set(val);
        }
        
        public String getDate(){
            return date.get();
        }
        
        public void setType(String val){
            type.set(val);
        }
        
        public String getType(){
            return type.get();
        }
    }
}

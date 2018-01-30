/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import static com.cbmwebdevelopment.main.Values.DATE_FORMAT;	
import static com.cbmwebdevelopment.main.Values.ORGANIZATION_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;

import org.controlsfx.control.Notifications;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.items.ItemFXMLController;
import com.cbmwebdevelopment.main.Values;
import com.cbmwebdevelopment.tablecontrollers.AuctionItemsTableViewController.AuctionItem;
import com.cbmwebdevelopment.tablecontrollers.AuctionsTableViewController.Auctions;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Auction {
	
	public ObservableList<String> getAuctionList(String val){
		ObservableList<String> auctionNames = FXCollections.observableArrayList();
		ObservableList<String> auctionIds = FXCollections.observableArrayList();
		String sql = null;
		if(val == null || val.trim().isEmpty()) {
			sql = "SELECT ID, NAME FROM AUCTIONS ORDER BY ID ASC";
		}else {
			sql = "SELECT ID, NAME FROM AUCTIONS WHERE NAME = ? OR NAME LIKE ? ORDER BY NAME ASC";
		}
		
		try {
			Connection conn = new DBConnection().connect();
			PreparedStatement ps = conn.prepareStatement(sql);
			// Add search parameters if the search field is not empty
			if(val != null && !val.trim().isEmpty()) {
				ps.setString(1, val);
				ps.setString(2,  "%" + val + "%");
			}
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				do {
					auctionIds.add(rs.getString("ID"));
					auctionNames.add(rs.getString("NAME"));
				}while(rs.next());
				
				// Set the auction IDs in the controller
				ItemFXMLController.auctionIds.setAll(auctionIds);
			}
			conn.close();
		}catch(SQLException ex) {
			Notifications.create().text("Error fetching results\nError: " + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return auctionNames;
	}
	
	/**
	 * Get the auction based on the ID.
	 * Returns a HashMap of values.
	 * @param id
	 * @return
	 */
	public HashMap<String, Object> getAuction(String id){
		HashMap<String, Object> auction = new HashMap<>();
		
		Connection conn = new DBConnection().connect();
		String sql = "SELECT ID, NAME, DESCRIPTION, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY, BUILDING_NAME, PUBLIC, TYPE, DATE_FORMAT(AUCTION_DATE, '%Y-%m-%d') AS 'AUCTION_DATE' FROM AUCTIONS WHERE ID = ? AND ORGANIZATION_ID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,  id);
			ps.setString(2,  Values.ORGANIZATION_ID);
			ResultSet rs = ps.executeQuery();
			ObservableList<AuctionItem> auctionItems = getAuctionItems(id);
			if(rs.next() && auctionItems != null) {
				auction.put("ID", rs.getString("ID"));
				auction.put("NAME", rs.getString("NAME"));
				auction.put("DESCRIPTION", rs.getString("DESCRIPTION"));
				auction.put("PRIMARY_ADDRESS", rs.getString("PRIMARY_ADDRESS"));
				auction.put("SECONDARY_ADDRESS", rs.getString("SECONDARY_ADDRESS"));
				auction.put("CITY", rs.getString("CITY"));
				auction.put("STATE", rs.getString("STATE"));
				auction.put("POSTAL_CODE", rs.getString("POSTAL_CODE"));
				auction.put("COUNTRY", rs.getString("COUNTRY"));
				auction.put("BUILDING_NAME", rs.getString("BUILDING_NAME"));
				auction.put("PUBLIC", rs.getBoolean("PUBLIC"));
				auction.put("TYPE", rs.getString("TYPE"));
				auction.put("AUCTION_DATE", rs.getString("AUCTION_DATE"));
				auction.put("AUCTION_ITEMS", auctionItems);
			}
		}catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}finally {
			try {
				conn.close();
			}catch(SQLException ex) {
				System.err.println(ex.getMessage());;
			}
		}
		return auction;
	}
	/**
	 * Create a new auction event.
	 * Returns a string value of the unique auction ID.
	 * @param controller
	 * @return
	 */
	public String saveNewAuction(AuctionFXMLController controller) {
		String auctionId = null;
		Connection conn = new DBConnection().connect();
		String sql = "INSERT INTO AUCTIONS (NAME, DESCRIPTION, ORGANIZATION_ID, PRIMARY_ADDRESS, SECONDARY_ADDRESS, CITY, STATE, POSTAL_CODE, COUNTRY, BUILDING_NAME, PUBLIC, TYPE, AUCTION_DATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, controller.title);
			ps.setString(2, controller.description);
			ps.setString(3, Values.ORGANIZATION_ID);
			ps.setString(4, controller.primaryAddress);
			ps.setString(5, controller.secondaryAddress);
			ps.setString(6, controller.city);
			ps.setString(7, controller.state);
			ps.setString(8, controller.postalCode);
			ps.setString(9, controller.country);
			ps.setString(10, controller.buildingName);
			ps.setBoolean(11, controller.isPublic);
			ps.setString(12, controller.type);
			ps.setString(13,controller.dateTime);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()) {
				auctionId = rs.getString(1);
			}
		}catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}finally {
			try {
				conn.close();
			}catch(SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}
		return auctionId;
	}
	
	/**
	 * Get the items that are associated with the selected auction.
	 * @param id
	 * @return
	 */
	public ObservableList<AuctionItem> getAuctionItems(String id){
		ObservableList<AuctionItem> data = FXCollections.observableArrayList();
		Connection conn = new DBConnection().connect();
		String sql = "SELECT ID, NAME, DESCRIPTION FROM AUCTION_ITEMS WHERE AUCTION_ID = ? AND ORGANIZATION_ID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, Values.ORGANIZATION_ID);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				do {
					Integer itemId = rs.getInt("ID");
					String itemName = rs.getString("NAME");
					String itemDescription = rs.getString("DESCRIPTION");
					data.add(new AuctionItem(itemId, itemName, itemDescription));
				}while(rs.next());
			}
		}catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}finally {
			try {
				conn.close();
			}catch(SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}
		return data;
	}
	/**
	 * Updates an existing auction event. 
	 * @param controller
	 */
	public boolean saveExistingAuction(AuctionFXMLController controller) {
		boolean updated = false;
		Connection conn = new DBConnection().connect();
		String sql = "UPDATE AUCTIONS SET TITLE = ?, DESCRIPTION = ?, TYPE = ?, PRIMARY_ADDRESS = ?, SECONDARY_ADDRESS = ?, CITY = ?, STATE = ?, POSTAL_CODE = ?, COUNTRY = ?, BUILDING_NAME = ?, PUBLIC = ?, TYPE = ?, AUCTION_DATE = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,  controller.title);
			ps.setString(2,  controller.description);
			ps.setString(3,  controller.type);
			ps.setString(4,  controller.primaryAddress);
			ps.setString(5,  controller.secondaryAddress);
			ps.setString(6,  controller.city);
			ps.setString(7,  controller.state);
			ps.setString(8,  controller.postalCode);
			ps.setString(9,  controller.country);
			ps.setString(10,  controller.buildingName);
			ps.setBoolean(11,  controller.isPublic);
			ps.setString(12,  controller.type);
			ps.setString(13,  controller.dateTime);
			int rs = ps.executeUpdate();
			if(rs > 0) {
				updated = true;
			}
		}catch(SQLException ex) {
			System.err.println(ex.getMessage());
		}finally {
			try {
				conn.close(); 
			}catch(SQLException ex) {
				System.err.println(ex.getMessage());
			}
		}
		return updated;
	}
    /**
     * Returns an observable list of the auction(s)
     * Can return filtered values
     * @param filterBy
     * @return 
     */
    protected ObservableList<Auctions> getAuctions(String filterBy){
        ObservableList<Auctions> data = FXCollections.observableArrayList();
        LocalDate localDate = LocalDate.now();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, NAME, AUCTION_DATE, TYPE FROM AUCTIONS WHERE ORGANIZATION_ID = ?";
        
        // If the results are being filtered
        if(!filterBy.equals("-")){
            sql += " AND ";
            switch(filterBy){
                case "Upcoming":
                    sql += "AUCTION_DATE >= " + DATE_FORMAT.format(localDate);
                    break;
                case "Current":
                    sql += "AUCTION_DATE = " + DATE_FORMAT.format(localDate);
                    break;
                case "Completed":
                    sql += "AUCTION_DATE < " + DATE_FORMAT.format(localDate);
                    break;
                case "Live Auction": 
                    sql += "`TYPE` = 'live'";
                    break;
                case "Silent Auction":
                    sql += "`TYPE` = 'silent'";
                    break;
                default:break;
            }
        }
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    data.add(new Auctions(rs.getInt("ID"), rs.getString("NAME"), rs.getString("AUCTION_DATE"), rs.getString("TYPE").substring(0,1).toUpperCase() + rs.getString("TYPE").substring(1)));
                }while(rs.next());
            }
        }catch(SQLException ex){
            Platform.runLater(()->{Notifications.create().text("Error fetching results" + "\n" + "Error Message: " + ex.getMessage()).showError();});
            System.err.println(ex.getMessage());
        }finally{
            try{
                conn.close();
            }catch(SQLException ex){
                System.err.println(ex.getMessage());
            }
        }
        return data;
    }
}

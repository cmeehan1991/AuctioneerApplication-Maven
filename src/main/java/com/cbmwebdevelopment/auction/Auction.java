/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.auction;

import com.cbmwebdevelopment.connections.DBConnection;
import static com.cbmwebdevelopment.main.Values.DATE_FORMAT;
import static com.cbmwebdevelopment.main.Values.ORGANIZATION_ID;
import com.cbmwebdevelopment.tablecontrollers.AuctionsTableViewController.Auctions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.Notifications;

/**
 *
 * @author cmeehan
 */
public class Auction {
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
                    sql = "AUCTION_DATE >= " + DATE_FORMAT.format(localDate);
                    break;
                case "Current":
                    sql = "AUCTION_DATE = " + DATE_FORMAT.format(localDate);
                    break;
                case "Completed":
                    sql = "AUCTION_DATE < " + DATE_FORMAT.format(localDate);
                    break;
                case "Live Auction": 
                    sql = "TYPE = live";
                    break;
                case "Silent Auction":
                    sql = "TYPE = silent";
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
            Notifications.create().text("Error fetching results" + "\n" + "Error Message: " + ex.getMessage()).showError();
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.items;

import com.cbmwebdevelopment.bid.BidFXMLController;
import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.main.Values;
import com.cbmwebdevelopment.tablecontrollers.ItemsTableController.AllItems;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author cmeehan
 */
public class Item {
    public void saveItem(ItemFXMLController controller) throws IOException {
        Connection conn = new DBConnection().connect();

        String sql = null;
        if (controller.itemNumber.trim().isEmpty() || controller.itemNumber == null) {
            sql = "INSERT INTO AUCTION_ITEMS(NAME, DESCRIPTION, RESERVE, SILENT_AUCTION, LIVE_AUCTION, CLOSED) VALUES(?,?,?,?,?,?)";
            try {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, controller.itemName);
                ps.setString(2, controller.itemDescription);
                ps.setString(3, controller.minimumBid);
                ps.setBoolean(4, controller.silentAuction);
                ps.setBoolean(5, controller.liveAuction);
                ps.setBoolean(6, false);
                System.out.println(ps);
                ps.executeUpdate();
                ResultSet key = ps.getGeneratedKeys();
                if (key.next()) {
                    controller.itemNumberTextField.setText(String.valueOf(key.getInt(1)));
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Item Saved");
                    alert.setHeaderText("Item " + String.valueOf(key.getInt(1)) + " was successfully saved.");
                    alert.setContentText("Close this window to continue");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        } else {
            sql = "UPDATE AUCTION_ITEMS SET NAME = ?, DESCRIPTION = ?, RESERVE = ?, SILENT_AUCTION = ?, LIVE_AUCTION = ?, CLOSED = ? WHERE ID = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, controller.itemName);
                ps.setString(2, controller.itemDescription);
                ps.setString(3, controller.minimumBid);
                ps.setBoolean(4, controller.silentAuction);
                ps.setBoolean(5, controller.liveAuction);
                ps.setBoolean(6, false);
                ps.setString(7, controller.itemNumber);
                int rs = ps.executeUpdate();
                if (rs > 0) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Item Saved");
                    alert.setHeaderText("Item " + controller.itemNumber + " was successfully updated.");
                    alert.setContentText("Close this window to continue");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

    }

    protected HashMap<String, String> getValues(String itemId) {
        HashMap<String, String> results = new HashMap<>();

        Connection conn = new DBConnection().connect();
        String sql = "SELECT AUCTION_ITEMS.NAME AS 'NAME', AUCTION_ITEMS.DESCRIPTION, AUCTIONS.NAME as 'AUCTION', AUCTION_ITEMS.RESERVE, AUCTION_ITEMS.SILENT_AUCTION, AUCTION_ITEMS.LIVE_AUCTION, AUCTION_ITEMS.CLOSED FROM AUCTION_ITEMS INNER JOIN AUCTIONS ON AUCTION_ITEMS.AUCTION_ID = AUCTIONS.ID WHERE AUCTION_ITEMS.ID =? AND AUCTION_ITEMS.ORGANIZATION_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, itemId);
            ps.setString(2, Values.ORGANIZATION_ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Has results");
                results.put("name", rs.getString("NAME"));
                results.put("description", rs.getString("DESCRIPTION"));
                results.put("reserve", rs.getString("RESERVE"));
                results.put("silentAuction", String.valueOf(rs.getBoolean("SILENT_AUCTION")));
                results.put("liveAuction", String.valueOf(rs.getBoolean("LIVE_AUCTION")));
                results.put("closed", String.valueOf(rs.getBoolean("CLOSED")));
                results.put("auction", rs.getString("AUCTION"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return results;
    }

    public void getItem(String id, BidFXMLController controller) {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT NAME, DESCRIPTION, CLOSED FROM AUCTION_ITEMS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                controller.itemNameTextField.setText(rs.getString("NAME"));
                controller.itemDescriptionTextField.setText(rs.getString("DESCRIPTION"));
                if (rs.getBoolean("CLOSED")) {
                    controller.submitWinnerButton.setDisable(true);
                } else {
                    controller.submitWinnerButton.setDisable(false);
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public ObservableList<AllItems> getAuctionItems(String terms, HashMap<String, String> filters) {

        // The obeservable list to be returned
        ObservableList<AllItems> data = FXCollections.observableArrayList();

        Connection conn = new DBConnection().connect();
        String sql = "SELECT AUCTION_ITEMS.ID, AUCTION_ITEMS.NAME, AUCTION_ITEMS.DESCRIPTION, IF(SILENT_AUCTION = TRUE, 'Silent Auction', 'Live Auction') AS 'TYPE',  IF(CLOSED = TRUE, 'Closed', 'Open') as 'STATUS' FROM AUCTION_ITEMS ORDER BY ID ASC";

        if (terms != null || filters != null) {
            sql += " WHERE ";
        }

        if (terms != null) {
            sql += "NAME = ? OR DESCRIPTION = ? OR NAME LIKE ? OR DESCRIPTION LIKE ?";
        }

        if (filters != null) {
            if (terms != null) {
                sql += " AND ";
            }

            sql += " CLOSED = ? AND SILENT_AUCTION = ? AND LIVE_AUCTION = ?";
        }

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (terms != null) {
                ps.setString(1, terms);
                ps.setString(2, terms);
                ps.setString(3, "%" + terms + "%");
                ps.setString(4, "%" + terms + "%");
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.addAll(new AllItems(rs.getInt("ID"), rs.getString("NAME"), rs.getString("DESCRIPTION"), rs.getString("TYPE"), rs.getString("STATUS")));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return data;
    }

    public HashMap<Integer, List<String>> itemInformation() {
        HashMap<Integer, List<String>> list = new HashMap<>();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, NAME, DESCRIPTION, IF(SILENT_AUCTION = TRUE, 'Silent Auction', 'Live Auction') AS 'AUCTION_TYPE', IF(CLOSED = TRUE, 'Closed', '') AS 'CLOSED' FROM AUCTION_ITEMS ORDER BY ID ASC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.put(rs.getInt("ID"), new ArrayList<String>() {
                        {
                            add(rs.getString("NAME"));
                            add(rs.getString("DESCRIPTION"));
                            add(rs.getString("AUCTION_TYPE"));
                            add(rs.getString("CLOSED"));
                        }
                    });
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return list;
    }

    public void removeItem(int id) {
        Connection conn = new DBConnection().connect();
        String sql = "DELETE FROM AUCTION_ITEMS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }

    }
}

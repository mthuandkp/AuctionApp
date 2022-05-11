/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LTM_02;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author ADMIN
 */
public class ConnectDB {
    private static String URL = "jdbc:mysql://localhost:3306/daugia?zeroDateTimeBehavior=convertToNull";
    private static String USER = "root";
    private static String PASS = "123456";
    private Connection conn = null; 
    

    public ConnectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến server");
            e.printStackTrace();
        }
    }
    
    public List<User> getAllUser() {
        List<User> listUser = new ArrayList<>();
         try {
            String qry = "SELECT * FROM `user`";
            PreparedStatement prestm = conn.prepareStatement(qry);
            ResultSet rs = prestm.executeQuery();

            if (rs == null) {
                return null;
            }

            while (rs.next()) {
                listUser.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getBoolean(5)));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return listUser;
    }
    
    public User login(String uname,String pass) {
        List<User> listUser = new ArrayList<>();
         try {
            String qry = "SELECT * FROM `user` WHERE `user`=? AND `pass`=?";
            PreparedStatement prestm = conn.prepareStatement(qry);
            prestm.setString(1, uname);
            prestm.setString(2, pass);
            ResultSet rs = prestm.executeQuery();

            if (rs == null || !rs.isBeforeFirst()) {
                return null;
            }
            
            if(rs.next()){
                return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getBoolean(5));
            }
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public User getUserById(int id) {
        List<User> listUser = new ArrayList<>();
         try {
            String qry = "SELECT * FROM `user` WHERE `id`=?";
            PreparedStatement prestm = conn.prepareStatement(qry);
            prestm.setInt(1, id);

            ResultSet rs = prestm.executeQuery();

            if (rs == null || !rs.isBeforeFirst()) {
                return null;
            }
            
            if(rs.next()){
                return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getBoolean(5));
            }
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public Product getAuctionProduct() {
        List<User> listUser = new ArrayList<>();
         try {
            String qry = "SELECT * FROM `product` WHERE `status`=0";
            PreparedStatement prestm = conn.prepareStatement(qry);
            ResultSet rs = prestm.executeQuery();

            if (rs == null || !rs.isBeforeFirst()) {
                return null;
            }
            
            if(rs.next()){
                return new Product(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getBoolean(4), rs.getInt(5), rs.getString(6));
            }
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("Không thể đóng kết nối : " + e);
        }
    }
    
    
    public static void main(String[] args) {
        ConnectDB con = new ConnectDB();
        System.out.println(con.login("user1", "13"));
    }

    void lockUser(int userId) {
       try {
            String qry = "UPDATE `user` SET `lockU`=false";
            PreparedStatement prestm = conn.prepareStatement(qry);
           prestm.executeUpdate();
           
          qry = "UPDATE `user` SET `lockU`=true WHERE `id` = ?";
            prestm = conn.prepareStatement(qry);
            prestm.setInt(1, userId);
           prestm.executeUpdate();
           
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void subtractMoney(int maxUser, int maxPrice) {
       try {
            String qry = "UPDATE `user` SET `lockU`=false";
            PreparedStatement prestm = conn.prepareStatement(qry);
           prestm.executeUpdate();
           
          qry = "UPDATE `user` SET `balance`=? WHERE `id` = ?";
          User user = getUserById(maxUser);
            prestm = conn.prepareStatement(qry);
            prestm.setInt(1, user.getBalance()- maxPrice);
            prestm.setInt(2, maxUser);
           prestm.executeUpdate();
           
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void buyProduct(int maxUser, int id) {
        try {
            String qry = "UPDATE `product` SET `status`=true,`user_id`=? WHERE `id` = ?";
            PreparedStatement prestm = conn.prepareStatement(qry);
            System.out.println(prestm);
           
          
            prestm.setInt(1, maxUser);
            prestm.setInt(2, id);
           prestm.executeUpdate();
           
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

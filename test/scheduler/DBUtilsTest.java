/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;


/**
 *
 * @author Dave
 */
@TestMethodOrder(OrderAnnotation.class)
public class DBUtilsTest {
    
    public DBUtilsTest() {
    }
    
    private static DBUtils dbInstance;
    
    @BeforeClass
    public static void setUpClass() throws SQLException {
        dbInstance = DBUtils.getInstance();
        dbInstance.openDB();
        dbInstance.setCurrentUser(new User(1, "test","test",1));
    }
    
    @AfterClass
    public static void tearDownClass() {
        //remove all test data
        try{
            Statement stmt = dbInstance.dbConnection.createStatement();
            stmt.executeUpdate("DELETE FROM appointment WHERE title LIKE 'TEST %'");
                    stmt = dbInstance.dbConnection.createStatement();
            stmt.executeUpdate("DELETE FROM customer WHERE customerName LIKE 'TEST %'");
                    stmt = dbInstance.dbConnection.createStatement();
            stmt.executeUpdate("DELETE FROM address WHERE address LIKE 'TEST %'");
                    stmt = dbInstance.dbConnection.createStatement();
            stmt.executeUpdate("DELETE FROM city WHERE city LIKE 'TEST %'");
                    stmt = dbInstance.dbConnection.createStatement();
            stmt.executeUpdate("DELETE FROM country WHERE country LIKE 'TEST %'");
                    stmt = dbInstance.dbConnection.createStatement();
            stmt.executeUpdate("DELETE FROM user WHERE userName LIKE 'TEST %'");
        }
        catch(SQLException sqle){
            System.out.println(sqle.getMessage());
        }
        
        dbInstance.closeDBConnection();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addAutoIncrement method, of class DBUtils.
     */
    @Test
    @Order(1)
    public void testAddAutoIncrement() {
        System.out.println("\naddAutoIncrement");
        dbInstance.addAutoIncrement();
    }

    /**
     * Test of insertUser method, of class DBUtils.
     */
    @Test
    @Order(2)
    public void testInsertUser() {
        System.out.println("\n--insertUser");
        String userName = "TEST USERNAME";
        String password = "TEST PW";
        Boolean active = true;
        int result = dbInstance.insertUser(userName, password, active);
        assertTrue(result > 0);
        System.out.println("User inserted, id="+ result + "\n");
    }

    /**
     * Test of insertCountry method, of class DBUtils.
     */
    @Test
    @Order(3)
    public void testInsertCountry() {
        System.out.println("\n--insertCountry");
        String country = "TEST COUNTRY";
        int result = dbInstance.insertCountry(country);
        assertTrue(result > 0);
        System.out.println("User inserted, id="+ result + "\n");
    }

    /**
     * Test of insertCity method, of class DBUtils.
     */
    @Test
    @Order(4)
    public void testInsertCity() {
        System.out.println("\n--insertCity");
        String city = "TEST CITY";
        int result = dbInstance.insertCity(city, 1);
        assertTrue(result > 0);
        System.out.println("User inserted, id="+ result + "\n");
    }

    /**
     * Test of insertAddress method, of class DBUtils.
     */
    @Test
    @Order(5)
    public void testInsertAddress() {
        System.out.println("\n--insertAddress");
        String address = "TEST ADDRESS";
        String address2 = "N/A";
        String postalCode = "";
        String phone = "";
        int result = dbInstance.insertAddress(address, address2, 1, postalCode, phone);
        assertTrue(result > 0);
        System.out.println("User inserted, id="+ result + "\n");
    }

    /**
     * Test of insertCustomer method, of class DBUtils.
     */
    @Test
    @Order(6)
    public void testInsertCustomer() {
        System.out.println("\n--insertCustomer");
        String customerName = "TEST CUSTOMER";
        boolean active = true;
        int result = dbInstance.insertCustomer(customerName, 1, active);
        assertTrue(result > 0);
        System.out.println("User inserted, id="+ result + "\n");
    }

    /**
     * Test of insertAppointment method, of class DBUtils.
     */
    @Test
    @Order(7)
    public void testInsertAppointment() {
        System.out.println("\n--insertAppointment");
        String title = "TEST APPOINTMENT";
        String description = "N/A";
        String location = "N/A";
        String contact = "N/A";
        String type = "N/A";
        String url = "N/A";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        int result = dbInstance.insertAppointment(1, 1, title, description, location, contact, type, url, start, end);
        assertTrue(result > 0);
        System.out.println("Appointment inserted, id="+ result + "\n");
    }

    /**
     * Test of updateItem method, of class DBUtils.
     */
    @Test
    @Order(8)
    public void testUpdateItem() {
        System.out.println("\n--updateItem");
        //insert appointment to update.
        String title = "TEST APPOINTMENT TO UPDATE";
        String description = "N/A";
        String location = "N/A";
        String contact = "N/A";
        String type = "N/A";
        String url = "N/A";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        int result = dbInstance.insertAppointment(1, 1, title, description, location, contact, type, url, start, end);
        System.out.println("Appointment inserted, id="+ result + "\n");
        //update DB
        try{
            PreparedStatement stmt = dbInstance.dbConnection.prepareStatement("UPDATE appointment SET " +
                    "customerId=?," +
                    "title=?,"       + 
                    "description=?," +
                    "location=?,"  +
                    "contact=?,"   +
                    "type=?,"      +
                    "url=?,"       +
                    "start=?,"     +
                    "end=?,"        +
                    "lastUpdate=?," +
                    "lastUpdateBy=?" +
                    " WHERE appointmentId=?");
            stmt.setInt(1, 1);
            stmt.setString(2, "TEST APPOINTMENT UPDATED");
            stmt.setString(3, "UPDATED");
            stmt.setString(4, "UPDATED");
            stmt.setString(5, "UPDATED");
            stmt.setString(6, "UPDATED");
            stmt.setString(7, "UPDATED");
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
            stmt.setInt(12, result);
            //execute the statement, 
            dbInstance.updateItem(stmt, 10);
        
            stmt = dbInstance.dbConnection.prepareStatement("SELECT * FROM appointment WHERE appointmentId=?");
            stmt.setInt(1, result);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                assertTrue(rs.getString("title").equals("TEST APPOINTMENT UPDATED"));
                System.out.println(rs.getString("title"));
            }
            else{
                fail("No Appointment updated with id=" + result);
            }
            
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test of deleteItem method, of class DBUtils.
     */
    @Test
    @Order(9)
    public void testDeleteItem() {
        System.out.println("\n--deleteItem");
        
        //insert appointment to delete.
        String title = "TEST APPOINTMENT TO DELETE";
        String description = "N/A";
        String location = "N/A";
        String contact = "N/A";
        String type = "N/A";
        String url = "N/A";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        int result = dbInstance.insertAppointment(1, 1, title, description, location, contact, type, url, start, end);
        System.out.println("Appointment inserted, id="+ result + "\n");
        
        try{
            dbInstance.deleteItem("appointment", result);
        
            PreparedStatement stmt = dbInstance.dbConnection.prepareStatement("SELECT * FROM appointment WHERE appointmentId=?");
            stmt.setInt(1, result);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                fail("TEST appointment delete failed (id=" + result + ")");
            }
            System.out.println("Appointment deleted, id="+ result + "\n");
            
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
}

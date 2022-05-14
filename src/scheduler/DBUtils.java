/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;
import java.sql.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author Dave
 */
public class DBUtils {
    
    private static DBUtils instance;
    public Connection dbConnection;
    private final static String connString = "jdbc:mysql://wgudb.ucertify.com:3306/U05r3Y";
    private final static String dbUser = "U05r3Y";
    private final static String dbPassword = "53688586490";
    public User currentUser;
    
    private DBUtils(){
        
    }
    
    public static DBUtils getInstance(){
        if (instance == null){
            instance = new DBUtils();
        }
        return instance;
    }
    
    public void setCurrentUser(User curUser){
        currentUser = curUser;
    }
    
    public void closeDBConnection(){
        try{
            if (dbConnection != null){
                dbConnection.close();
            }
        } catch (SQLException e){
            System.out.println(e);
        }
        
    }
    
    public void openDB(){
        try{
            if (dbConnection == null){
                Properties props = new Properties();
                props.put("user", dbUser);
                props.put("password", dbPassword);
                props.put("autoReconnect", "true");
                //Scheduler.dbConnection = DriverManager.getConnection(connString, user, password);
                dbConnection = DriverManager.getConnection(connString, props);
            }
        } catch (SQLException e){
            System.out.println(e.toString() + "\nConnection to DB Failed.");
            
        }
        
    }
    
    //ensure tables have auto_increment on ID cols
    public void addAutoIncrement(){
        String[] tables = {
            "country",
            "city",
            "address",
            "customer",
            "appointment",
            "user"
        };
        
        try{
            Statement stmt = dbConnection.createStatement();
            for (String table : tables){
                stmt.execute("ALTER TABLE " + table + "\n" +
                            "MODIFY " + table + "Id integer NOT NULL AUTO_INCREMENT;");
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public int insertCountry(String country){
        
        try{
            //get a timestamp for now
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            String stmtString = "INSERT INTO country (country," + "createDate,createdBy,lastUpdate,lastUpdateBy" +  ") VALUES (?," + "?,?,?,?)" ;
            PreparedStatement stmt = dbConnection.prepareStatement(stmtString, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, country);
            stmt.setTimestamp(2, now);
            stmt.setString(3, currentUser.getUserName());
            stmt.setTimestamp(4, now);
            stmt.setString(5, currentUser.getUserName());
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0)
                throw new SQLException("Insert country failed, no rows affected.");
            else{
                ResultSet id = stmt.getGeneratedKeys();
                if(id.next())
                    return id.getInt(1);
            }
        }catch (SQLException e){
            System.out.println("Insert Country: \n" + e.getMessage());
        }
        
        return -1;
    }
    
    public int insertCity(String city, int countryId){
        
        try{
            //get a timestamp for now
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            String stmtString = "INSERT INTO city (city,countryId," + "createDate,createdBy,lastUpdate,lastUpdateBy" +  ") VALUES (?,?," + "?,?,?,?)" ;
            PreparedStatement stmt = dbConnection.prepareStatement(stmtString, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, city);
            stmt.setInt(2, countryId);
            stmt.setTimestamp(3, now);
            stmt.setString(4, currentUser.getUserName());
            stmt.setTimestamp(5, now);
            stmt.setString(6, currentUser.getUserName());
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0)
                throw new SQLException("Insert city failed, no rows affected.");
            else{
                ResultSet id = stmt.getGeneratedKeys();
                if(id.next())
                    return id.getInt(1);
            }
        }catch (SQLException e){
            System.out.println("Insert City: \n" + e.getMessage());
        }
        
        return -1;
    }
    
    public int insertAddress(String address, String address2, int cityId, String postalCode, String phone){
        
        try{
            //get a timestamp for now
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            String stmtString = "INSERT INTO address (address,address2,cityId,postalCode,phone," + "createDate,createdBy,lastUpdate,lastUpdateBy" +  ") VALUES (?,?,?,?,?," + "?,?,?,?)" ;
            PreparedStatement stmt = dbConnection.prepareStatement(stmtString, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, address);
            stmt.setString(2, address2);
            stmt.setInt(3, cityId);
            stmt.setString(4, postalCode);
            stmt.setString(5, phone);
            stmt.setTimestamp(6, now);
            stmt.setString(7, currentUser.getUserName());
            stmt.setTimestamp(8, now);
            stmt.setString(9, currentUser.getUserName());
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0)
                throw new SQLException("Insert address failed, no rows affected.");
            else{
                ResultSet id = stmt.getGeneratedKeys();
                if(id.next())
                    return id.getInt(1);
            }
        }catch (SQLException e){
            System.out.println("Insert Address: \n" + e.getMessage());
        }
        
        return -1;
    }
    
    public int insertCustomer(String customerName, int addressId, boolean active){
        
        try{
            //get a timestamp for now
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            String stmtString = "INSERT INTO customer (customerName,addressId,active," + "createDate,createdBy,lastUpdate,lastUpdateBy" +  ") VALUES (?,?,?," + "?,?,?,?)" ;
            PreparedStatement stmt = dbConnection.prepareStatement(stmtString, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, customerName);
            stmt.setInt(2, addressId);
            stmt.setInt(3, (active? 1:0));
            stmt.setTimestamp(4, now);
            stmt.setString(5, currentUser.getUserName());
            stmt.setTimestamp(6, now);
            stmt.setString(7, currentUser.getUserName());
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0)
                throw new SQLException("Insert customer failed, no rows affected.");
            else{
                ResultSet id = stmt.getGeneratedKeys();
                if(id.next())
                    return id.getInt(1);
            }
        }catch (SQLException e){
            System.out.println("Insert Customer: \n" + e.getMessage());
        }
        
        return -1;
    }
    
    public int insertAppointment(int customerId, int userId, String title, 
            String description, String location, String contact, String type, 
            String url, LocalDateTime start, LocalDateTime end){
        
        try{
            //get a timestamp for now
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            String stmtString = "INSERT INTO appointment (customerId,userId,title,description,location,contact,type,url,start,end," + "createDate,createdBy,lastUpdate,lastUpdateBy" +  ") VALUES (?,?,?,?,?,?,?,?,?,?," + "?,?,?,?)" ;
            PreparedStatement stmt = dbConnection.prepareStatement(stmtString, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, customerId);
            stmt.setInt(2, userId);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setString(5, location);
            stmt.setString(6, contact);
            stmt.setString(7, type);
            stmt.setString(8, url);
            stmt.setTimestamp(9, Timestamp.valueOf(start));
            stmt.setTimestamp(10, Timestamp.valueOf(end));
            stmt.setTimestamp(11, now);
            stmt.setString(12, currentUser.getUserName());
            stmt.setTimestamp(13, now);
            stmt.setString(14, currentUser.getUserName());
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0)
                throw new SQLException("Insert appointment failed, no rows affected.");
            else{
                ResultSet id = stmt.getGeneratedKeys();
                if(id.next())
                    return id.getInt(1);
            }
        }catch (SQLException e){
            System.out.println("Insert Appointment: \n" + e.getMessage());
        }
        
        return -1;
    }
    
    public int insertUser(String userName, String password, Boolean active){
        
        try{
            //get a timestamp for now
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            String stmtString = "INSERT INTO user (userName,password,active," + "createDate,createdBy,lastUpdate,lastUpdateBy" +  ") VALUES (?,?,?," + "?,?,?,?)" ;
            PreparedStatement stmt = dbConnection.prepareStatement(stmtString, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.setInt(3, (active? 1:0));
            stmt.setTimestamp(4, now);
            stmt.setString(5, currentUser.getUserName());
            stmt.setTimestamp(6, now);
            stmt.setString(7, currentUser.getUserName());
            
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0)
                throw new SQLException("Insert user failed, no rows affected.");
            else{
                ResultSet id = stmt.getGeneratedKeys();
                if(id.next())
                    return id.getInt(1);
            }
        }catch (SQLException e){
            System.out.println("Insert User: \n" + e.getMessage());
        }
        
        return -1;
    }
    
    public void deleteItem(String table, int id){
        try{
            Statement stmt = dbConnection.createStatement();
            stmt.execute("DELETE FROM " + table + " WHERE " + table + "Id=" + id + ";");
        }catch (SQLException e){
            System.out.println("Delete Item: \n" + e);
        }
    }
    
    public void updateItem(PreparedStatement stmt, int nextIndex){
        try{
            //get a timestamp for now
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(nextIndex++, now);
            stmt.setString(nextIndex, currentUser.getUserName());
            
            //System.out.println(stmt);
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0)
                throw new SQLException("Update failed, no rows affected.");
            
        }catch (SQLException e){
            System.out.println("Update Item: \n" + e.getMessage());
        }
    }
    
    public void initData(){
        HashMap<Integer,String> stringDict = new HashMap();
        try{
            //Countries
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM country");
            while(rs.next()){
                Scheduler.getAllCountries().add(new Country(rs.getInt("countryId"),rs.getString("country")));
                stringDict.put(rs.getInt("countryId"), rs.getString("country"));
            }
            
            
            //Cities
            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM city");
            while(rs.next()){
                Scheduler.getAllCities().add(new City(rs.getInt("cityId"),rs.getString("city"),rs.getInt("countryId"), stringDict.get(rs.getInt("countryId"))));
            }
            stringDict.clear();
            Scheduler.getAllCities().forEach((city) -> stringDict.put(city.getCityId(), city.getAsString())); //using lambda expression to be more concise than traditional for-each loop 
            
            
            //Addresses
            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM address");
            while(rs.next()){
                Scheduler.getAllAddresses().add(new Address(rs.getInt("addressId"),rs.getString("address"),rs.getString("address2"),rs.getInt("cityId"),rs.getString("postalCode"),rs.getString("phone"), stringDict.get(rs.getInt("cityId"))));
            }
            stringDict.clear();
            Scheduler.getAllAddresses().forEach((address) -> stringDict.put(address.getAddressId(),address.getAsString())); //using lambda expression to be more concise than traditional for-each loop 
            
            
            //Customers
            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM customer");
            while(rs.next()){
                Scheduler.getAllCustomers().add(new Customer(rs.getInt("customerId"),rs.getString("customerName"),rs.getInt("addressId"),rs.getInt("active"), stringDict.get(rs.getInt("addressId"))));
            }
            stringDict.clear();
            //Scheduler.getAllCustomers().forEach((customer) -> stringDict.put(customer.getCustomerId(),customer.toString())); //using lambda expression to be more concise than traditional for-each loop 
            
            
            //Appointments
            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM appointment");
            while(rs.next()){
                Scheduler.getAllAppointments().add(new Appointment(rs.getInt("appointmentId"),
                        rs.getInt("customerId"),rs.getInt("userId"),rs.getString("title"),
                        rs.getString("description"),rs.getString("location"),
                        rs.getString("contact"),rs.getString("type"),rs.getString("url"),
                        rs.getTimestamp("start").toLocalDateTime(),
                        rs.getTimestamp("end").toLocalDateTime()));
            }
            
            //Users
            stmt = dbConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM user");
            while(rs.next()){
                Scheduler.getAllUsers().add(new User(rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getInt("active")));
            }
        }catch (SQLException e){
            System.out.println(e + "\n   in InitData()");
        }
    }
}


    


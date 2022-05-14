/**
 * Scheduler Project
 * David Smith
 * C195 2020
 * 
 */
package scheduler;

import java.io.IOException;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
/**
 *
 * @author Dave
 */
public class Scheduler extends Application {
    private static ObservableList<Country> allCountries = FXCollections.observableArrayList();
    private static ObservableList<City> allCities = FXCollections.observableArrayList();
    private static ObservableList<Address> allAddresses = FXCollections.observableArrayList();
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<User> allUsers = FXCollections.observableArrayList();
    
    public static DBUtils dbInstance;
    
    private static Locale curLocale;
    
    public static HashMap<Class<?>,Object> controllerInstanceMap = new HashMap();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        dbInstance = DBUtils.getInstance();
        dbInstance.openDB();
        dbInstance.addAutoIncrement(); //ensures auto incrementing on table IDs.
        
        //get current locale
        curLocale = Locale.getDefault();
       
        //Initialize Data Objects
        if (dbInstance != null){
            dbInstance.initData();
        }
        
        launch(args);
         
       //close DB connection
        dbInstance.closeDBConnection();
    }
    
    @Override
    public void start(Stage logInStage) throws Exception {
        
        //log in stuff
        String viewPath = "../view/log_in.fxml";
        
        ResourceBundle rb = ResourceBundle.getBundle("SchedulerLogIn", curLocale);
        
        Parent root = FXMLLoader.load(getClass().getResource(viewPath), rb);
        Scene logInScene = new Scene(root);
        logInStage.setScene(logInScene);
        logInStage.setTitle(rb.getString("logintitle"));
        logInStage.show();
    }
    
    public static void createCountry(String country){
        allCountries.add(new Country(country));
    }
    
    public static void createCity(String city, int countryId, String country){
        allCities.add(new City(city, countryId, country));
    }
    
    public static void createAddress(String address, String address2, int cityId, 
                                String postalCode, String phone, String city){
        allAddresses.add(new Address(address, address2, cityId, postalCode, phone, city));
    }
    
    public static void createCustomer(String customerName, int addressId,
                                boolean active, String address){
        allCustomers.add(new Customer(customerName, addressId, active, address));
    }
    
    public static void createAppointment(int customerId, int userId, String title, 
            String description, String location, String contact, String type, 
            String url, LocalDateTime start, LocalDateTime end){
        
        allAppointments.add(new Appointment(customerId, userId, title, 
            description, location, contact, type, url, start, end));
    }
    
    public static void createUser(String userName, String password, boolean active){
        allUsers.add(new User(userName, password, active));
    }
    
    public static void updateCity(int id, String cityName, int countryId, String country){
        //update object
        City uCity = allCities.filtered((city) -> city.getCityId() == id).get(0);
        uCity.setCity(cityName);
        uCity.setCountryId(countryId);
        uCity.setCountry(country);
        //update DB
        try{
            PreparedStatement stmt = dbInstance.dbConnection.prepareStatement("UPDATE city SET " +
                    "city=?,"  +
                    "countryId=?,"         +
                    "lastUpdate=?,"     +
                    "lastUpdateBy=?"   +
                    " WHERE cityId=?");
            stmt.setString(1, cityName);
            stmt.setInt(2, countryId);
            stmt.setInt(5, id);
            //execute the statement, 
            dbInstance.updateItem(stmt, 3);
        
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void updateAddress(int id, String address1, String address2, int cityId, 
                                String postalCode, String phone, String city){
        //update object
        Address uAddress = allAddresses.filtered((address) -> address.getAddressId() == id).get(0);
        uAddress.setAddress(address1);
        uAddress.setAddress2(address2);
        uAddress.setCityId(cityId);
        uAddress.setPostalCode(postalCode);
        uAddress.setPhone(phone);
        uAddress.setCity(city);
        //update DB
        try{
            PreparedStatement stmt = dbInstance.dbConnection.prepareStatement("UPDATE address SET " +
                    "address=?,"     +
                    "address2=?,"    +
                    "cityId=?,"       +
                    "postalCode=?,"  +
                    "phone=?,"       +
                    "lastUpdate=?,"   +
                    "lastUpdateBy=?" +
                    " WHERE addressId=?");
            stmt.setString(1, address1);
            stmt.setString(2, address2);
            stmt.setInt(3, cityId);
            stmt.setString(4, postalCode);
            stmt.setString(5, phone);
            stmt.setInt(8, id);
            //execute the statement, 
            dbInstance.updateItem(stmt, 6);
        
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void updateCustomer(int id, String customerName, int addressId, boolean active, String address){
        //update object
        Customer uCustomer = allCustomers.filtered((customer) -> customer.getCustomerId() == id).get(0);
        uCustomer.setCustomerName(customerName);
        uCustomer.setAddressId(addressId);
        uCustomer.setActive(active);
        uCustomer.setAddress(address);
        //update DB
        try{
            PreparedStatement stmt = dbInstance.dbConnection.prepareStatement("UPDATE customer SET " +
                    "customerName=?,"  +
                    "addressId=?,"     +
                    "active=?,"         +
                    "lastUpdate=?,"     +
                    "lastUpdateBy=?"   +
                    " WHERE customerId=?");
            stmt.setString(1, customerName);
            stmt.setInt(2, addressId);
            stmt.setInt(3, (active? 1 : 0));
            stmt.setInt(6, id);
            //execute the statement, 
            dbInstance.updateItem(stmt, 4);
        
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void updateAppointment(int id, int customerId, String title, 
            String description, String location, String contact, String type, 
            String url, LocalDateTime start, LocalDateTime end){
        //update object
        Appointment uAppointment = allAppointments.filtered((appointment) -> appointment.getAppointmentId() == id).get(0);
        uAppointment.setCustomerId(customerId);
        uAppointment.setTitle(title);
        uAppointment.setDescription(description);
        uAppointment.setLocation(location);
        uAppointment.setContact(contact);
        uAppointment.setType(type);
        uAppointment.setUrl(url);
        
        LocalDate oldDate = uAppointment.getStart().toLocalDate();
        uAppointment.setStart(start);
        uAppointment.setEnd(end);
        //if changing start date...
        if (! uAppointment.getStart().toLocalDate().equals(oldDate)){
            refreshTables();
        }
        
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
            stmt.setInt(1, customerId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, location);
            stmt.setString(5, contact);
            stmt.setString(6, type);
            stmt.setString(7, url);
            stmt.setTimestamp(8, Timestamp.valueOf(start));
            stmt.setTimestamp(9, Timestamp.valueOf(end));
            stmt.setInt(12, id);
            //execute the statement, 
            dbInstance.updateItem(stmt, 10);
        
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void updateUser(int id, String userName, String password, boolean active){
        //update object
        User uUser = allUsers.filtered((user) -> user.getUserId() == id).get(0);
        uUser.setUserName(userName);
        uUser.setPassword(password);
        uUser.setActive(active);
        //update DB
        try{
            PreparedStatement stmt = dbInstance.dbConnection.prepareStatement("UPDATE user SET " +
                    "userName=?,"    +
                    "password=?,"    +
                    "active=?,"       +
                    "lastUpdate=?,"   +
                    "lastUpdateBy=?" +
                    " WHERE userId=?");
            stmt.setString(1, userName);
            stmt.setString(2, password);
            stmt.setInt(3, (active? 1 : 0));
            stmt.setInt(6, id);
            //execute the statement, 
            dbInstance.updateItem(stmt, 4);
        
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void deleteCountry(Country country){
        dbInstance.deleteItem("country", country.getCountryId());
        allCountries.remove(country);
    }
    
    public static void deleteCity(City city){
        dbInstance.deleteItem("city", city.getCityId());
        allCities.remove(city);
    }
    
    public static void deleteAddress(Address address){
        dbInstance.deleteItem("address", address.getAddressId());
        allAddresses.remove(address);
    }
    
    public static void deleteCustomer(Customer customer){
        int customerId = customer.getCustomerId();
        
        //since constraints in DB are ON DELETE RESTRICT, have to CASCADE 
        //customer deletions manually (delete appointments first)
        ObservableList<Appointment> deleteApts = allAppointments.filtered(apt -> apt.getCustomerId() == customerId);
        while(deleteApts.size() > 0){
            deleteAppointment(deleteApts.get(0));
        }
        
        dbInstance.deleteItem("customer", customerId );
        allCustomers.remove(customer);
    }
    
    public static void deleteAppointment(Appointment appointment){
        dbInstance.deleteItem("appointment", appointment.getAppointmentId());
        allAppointments.remove(appointment);
    }
    
    public static void deleteUser(User user){
        dbInstance.deleteItem("user", user.getUserId());
        allUsers.remove(user);
    }
    
    /**
     * @return the list of all Countries
     */
    public static ObservableList<Country> getAllCountries(){
        return allCountries;
    }
    
    /**
     * @return the list of all Cities
     */
    public static ObservableList<City> getAllCities(){
        return allCities;
    }
    
    /**
     * @return the list of all Addresses
     */
    public static ObservableList<Address> getAllAddresses(){
        return allAddresses;
    }
    
    /**
     * @return the list of all Customers
     */
    public static ObservableList<Customer> getAllCustomers(){
        return allCustomers;
    }
    
    /**
     * @return the list of all Appointments
     */
    public static ObservableList<Appointment> getAllAppointments(){
        return allAppointments;
    }
    
    /**
     * @return the list of all Users
     */
    public static ObservableList<User> getAllUsers(){
        return allUsers;
    }
    
    public static void openAlert(FXMLLoader loader, String alertTitle, String alertText){
        try{
            Parent root = (Parent)loader.load();
            AlertController controller = loader.getController();

            Scene alertScene = new Scene(root);
            Stage alertStage = new Stage();
            alertStage.setScene(alertScene);

            alertStage.setTitle(alertTitle);
            controller.setAlertText(alertText);
            alertStage.show();
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }
    
    public static void refreshTables(){
        
        controllerInstanceMap.forEach((Class<?> cntrlClass, Object obj) ->
                ((SchedulerController)obj).refreshTables()
                );
    }
    
}

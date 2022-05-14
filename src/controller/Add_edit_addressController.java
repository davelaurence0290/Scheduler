/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import static java.lang.Integer.parseInt;
import scheduler.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class Add_edit_addressController extends SchedulerController implements Initializable {

    @FXML
    private TableView cityTable;
    @FXML
    private TableView addressTable;
    @FXML
    private Button addAddressDoneBtn;
    @FXML
    private Label addressWarning;
    @FXML
    private TextField searchCityTextBox;
    @FXML
    private Button newCityBtn;
    @FXML
    private Button editCityBtn;
    @FXML
    private TextField searchAddressTextBox;
    @FXML
    private TextField addressLine1TextBox;
    @FXML
    private TextField addressLine2TextBox;
    @FXML
    private TextField addressPostalCodeTextBox;
    @FXML
    private TextField addressPhoneTextBox;
    @FXML
    private Button saveAddressBtn;
    @FXML
    private Button editAddressBtn;
    @FXML
    private TextField addressIdTextBox;
    @FXML
    private TableColumn cityTableId;
    @FXML
    private TableColumn cityTableCity;
    @FXML
    private TableColumn addressTableId;
    @FXML
    private TableColumn addressTableLine1;
    @FXML
    private TableColumn addressTableCity;
    @FXML
    private TableColumn addressTablePostalCode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //register this object in instancemap for reference
        scheduler.Scheduler.controllerInstanceMap.put(this.getClass(),this);
        
        //populate tables
        
        cityTableId.setCellValueFactory(new PropertyValueFactory<>("cityId"));
        cityTableCity.setCellValueFactory(new PropertyValueFactory<>("asString"));
        cityTable.setItems(scheduler.Scheduler.getAllCities());
        cityTable.setPlaceholder(new Label("No Cities Found"));
        
        
        addressTableId.setCellValueFactory(new PropertyValueFactory<>("addressId"));
        addressTableLine1.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressTableCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        addressTablePostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        addressTable.setItems(scheduler.Scheduler.getAllAddresses());
        addressTable.setPlaceholder(new Label("No Addresses Found"));
    }    

    @FXML
    private void close(ActionEvent event) {
        ((Stage) addAddressDoneBtn.getScene().getWindow()).close();
    }

    @FXML
    private void searchCity(ActionEvent event) {
        String searchQuery = searchCityTextBox.getText().toUpperCase();
        int searchID = -1;
        try{
            searchID = Integer.parseInt(searchQuery);
        }
        catch (NumberFormatException e){
            //
        }
        final int finalSearchID = searchID;
        
        //If blank, reset table
        if (searchQuery.equals(""))
            cityTable.setItems(scheduler.Scheduler.getAllCities());
        //otherwise, search
        else{
            FilteredList<City> filteredPartsTable = new FilteredList<>(scheduler.Scheduler.getAllCities(), city -> 
                ( city.getCity().contains(searchQuery) ||
                  city.getCountry().contains(searchQuery) ||
                  city.getCityId() == finalSearchID) );//using lambda for predicate of FilteredList contructor
            cityTable.setItems(filteredPartsTable);
        }
    }

    @FXML
    private void searchAddress(ActionEvent event) {
        String searchQuery = searchAddressTextBox.getText().toUpperCase();
        int searchID = -1;
        try{
            searchID = Integer.parseInt(searchQuery);
        }
        catch (NumberFormatException e){
            //
        }
        final int finalSearchID = searchID;
        
        //If blank, reset table
        if (searchQuery.equals(""))
            addressTable.setItems(scheduler.Scheduler.getAllAddresses());
        //otherwise, search
        else{
            FilteredList<Address> filteredPartsTable = new FilteredList<>(scheduler.Scheduler.getAllAddresses(), address -> 
                (address.getAddress().contains(searchQuery) ||
                 address.getAddress2().contains(searchQuery) ||
                 address.getCity().contains(searchQuery) ||
                 address.getPostalCode().equals(searchQuery) ||
                 address.getAddressId() == finalSearchID )
                    );//using lambda for predicate of FilteredList contructor
            addressTable.setItems(filteredPartsTable);
        }
    }

    @FXML
    private void saveAddress(ActionEvent event) {
        try{
            //parse supplied data
            String id = addressIdTextBox.getText();
            String line1 = addressLine1TextBox.getText().toUpperCase();
            String line2 = addressLine2TextBox.getText().toUpperCase();
            String postalCode = addressPostalCodeTextBox.getText().toUpperCase();
            String phone = addressPhoneTextBox.getText().toUpperCase();
            City city = (City)cityTable.getSelectionModel().getSelectedItem();
            int cityId = city.getCountryId();
            String cityName = city.getAsString();
            
            
            addressWarning.setText("");
            if (id.equals(""))//means we are creating new object.
            {
                scheduler.Scheduler.createAddress(line1, line2, cityId, postalCode, phone, cityName);
            }
            else{ //means we are modifying object, not creating new
                int addressId = parseInt(id);
                scheduler.Scheduler.updateAddress(addressId, line1, line2, cityId, postalCode, phone, cityName);
                
                //update customers that were using this addressId with new address string
                scheduler.Scheduler.getAllCustomers()
                        .filtered(customer -> customer.getAddressId() == addressId)
                        .forEach(customer -> customer.setAddress(
                        scheduler.Scheduler.getAllAddresses().filtered(address -> address.getAddressId() == addressId).get(0).getAsString()
                        ));
            }
            
            addressIdTextBox.clear();
            addressLine1TextBox.clear();
            addressLine2TextBox.clear();
            addressPostalCodeTextBox.clear();
            addressPhoneTextBox.clear();            
        }
        catch (IllegalArgumentException iae){
            scheduler.Scheduler.openAlert(new FXMLLoader(getClass().getResource("../view/alert.fxml")), "Illegal Input", iae.getMessage());
        }
        catch (Exception e){
                System.out.println("error");
                addressWarning.setText("Check supplied data types, ensure desired city is selected at right.");
                //System.out.println(e);
        }
        
        scheduler.Scheduler.refreshTables();
    }

    @FXML
    private void editAddress(ActionEvent event) {
        try{
            Address address = (Address) addressTable.getSelectionModel().getSelectedItem();
            //enter data to edit in text fields
            setAddressId(address.getAddressId());
            setLine1(address.getAddress());
            setLine2(address.getAddress2());
            setPhone(address.getPhone());
            setPostalCode(address.getPostalCode());
            setCitySelected(address.getCityId());
            
            addressWarning.setText("");
        }
        catch (Exception e){
                System.out.println(e.getMessage());
                addressWarning.setText("Select Address to edit");
                //System.out.println(e);
        }
    }
    
    @FXML
    private void addCity(ActionEvent event) {
        try{
            Stage addPartStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../view/Add_edit_city.fxml"));            
            Scene addPartScene = new Scene(root);
            addPartStage.setScene(addPartScene);
            addPartStage.setTitle("Manage Cities");
            addPartStage.show();
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }

    @FXML
    private void editCity(ActionEvent event) {
        try{
            FXMLLoader loader;
            if (cityTable.getSelectionModel().getSelectedItem() != null){
                loader = new FXMLLoader(getClass().getResource("../view/Add_edit_city.fxml"));
            
                Parent root = loader.load();
                Add_edit_cityController controller = loader.getController();

                City selectedCity = (City)cityTable.getSelectionModel().getSelectedItem();

                controller.setCityId(selectedCity.getCityId());
                controller.setCityName(selectedCity.getCity());
                controller.setCountrySelected(selectedCity.getCountryId());

                Scene manageCityScene = new Scene(root);
                Stage manageCityStage = new Stage();
                manageCityStage.setScene(manageCityScene);
                manageCityStage.setTitle("Manage Cities");
                manageCityStage.show();
            }
            else{
                loader = new FXMLLoader(getClass().getResource("../view/alert.fxml"));
                scheduler.Scheduler.openAlert(loader, "Edit City Failed","No City selected. Please select city to edit.");
            }
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }
    
    /**
     * @param addressId the addressId to set
     */
    public void setAddressId(int addressId){
        addressIdTextBox.setText(Integer.toString(addressId));
    }
    
    /**
     * @param line1 the Address line 1 to set
     */
    public void setLine1(String line1){
        addressLine1TextBox.setText(line1);
    }
    
    /**
     * @param line2 the Address line 2 to set
     */
    public void setLine2(String line2){
        addressLine2TextBox.setText(line2);
    }
    
    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode){
        addressPostalCodeTextBox.setText(postalCode);
    }
    
    /**
     * @param phone the phone number to set
     */
    public void setPhone(String phone){
        addressPhoneTextBox.setText(phone);
    }
    
    /**
     * @param cityId the cityId to select in cityTable
     */
    public void setCitySelected(int cityId){
        cityTable.getSelectionModel().select(scheduler.Scheduler.getAllCities().stream().filter(city -> city.getCityId() == cityId).findAny().get());
    }
    
    @Override
    public void refreshTables(){
        addressTable.refresh();
        cityTable.refresh();
    }
}

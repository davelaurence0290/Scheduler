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
import javafx.scene.control.CheckBox;
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
public class Add_edit_customerController extends SchedulerController implements Initializable {

    @FXML
    private TableView addressTable;
    @FXML
    private TableView customerTable;
    @FXML
    private Button addCustomerDoneBtn;
    @FXML
    private Label customerWarning;
    @FXML
    private TextField searchAddressTextBox;
    @FXML
    private Button newAddressBtn;
    @FXML
    private Button editAddressBtn;
    @FXML
    private TextField searchCustomerTextBox;
    @FXML
    private TextField customerNameTextBox;
    @FXML
    private Button saveCustomerBtn;
    @FXML
    private Button editCustomerBtn;
    @FXML
    private Button deleteCustomerBtn;
    @FXML
    private Button confirmDeleteCustomerBtn;
    @FXML
    private TextField customerIdTextBox;
    @FXML
    private CheckBox customerActiveChkBox;
    @FXML
    private TableColumn addressTableId;
    @FXML
    private TableColumn addressTableLine1;
    @FXML
    private TableColumn addressTableCity;
    @FXML
    private TableColumn customerTableId;
    @FXML
    private TableColumn customerTableName;
    @FXML
    private TableColumn customerTableAddressId;
    @FXML
    private TableColumn customerTableActive;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //register this object in instancemap for reference
        scheduler.Scheduler.controllerInstanceMap.put(this.getClass(),this);
        
        //populate tables
        
        addressTableId.setCellValueFactory(new PropertyValueFactory<>("addressId"));
        addressTableLine1.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressTableCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        addressTable.setItems(scheduler.Scheduler.getAllAddresses());
        addressTable.setPlaceholder(new Label("No Addresses Found"));
        
        customerTableId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerTableName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerTableAddressId.setCellValueFactory(new PropertyValueFactory<>("addressId"));
        customerTableActive.setCellValueFactory(new PropertyValueFactory<>("active"));
        customerTable.setItems(scheduler.Scheduler.getAllCustomers());
        customerTable.setPlaceholder(new Label("No Customers Found"));
        
        customerTable.getSelectionModel().selectedItemProperty().addListener( 
                (observable, oldvalue, newvalue) -> hideConfirmDeleteBtn()); //using lambda for consumer of .addListener function
    }    

    private void hideConfirmDeleteBtn(){
        confirmDeleteCustomerBtn.setVisible(false);
    }
    
    @FXML
    private void close(ActionEvent event) {
        ((Stage) addCustomerDoneBtn.getScene().getWindow()).close();
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
                 address.getAddressId() == finalSearchID )
                    );//using lambda for predicate of FilteredList contructor
            addressTable.setItems(filteredPartsTable);
        }
    }

    @FXML
    private void searchCustomer(ActionEvent event) {
        String searchQuery = searchCustomerTextBox.getText().toUpperCase();
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
            customerTable.setItems(scheduler.Scheduler.getAllCustomers());
        //otherwise, search
        else{
            FilteredList<Customer> filteredPartsTable = new FilteredList<>(scheduler.Scheduler.getAllCustomers(), customer -> 
                ( customer.getCustomerName().contains(searchQuery) ||
                  customer.getCustomerId() == finalSearchID) );
            customerTable.setItems(filteredPartsTable);//using lambda for predicate of FilteredList contructor
        }
    }

    @FXML
    private void saveCustomer(ActionEvent event) {
        try{
            //parse supplied data
            String id = customerIdTextBox.getText();
            String customerName = customerNameTextBox.getText().toUpperCase();
            boolean active = customerActiveChkBox.isSelected();
            Address address = (Address)addressTable.getSelectionModel().getSelectedItem();
            int addressId = address.getAddressId();
            String addressString = address.getAsString();
            
            customerWarning.setText("");
            if (id.equals(""))//means we are creating new object.
            {
                scheduler.Scheduler.createCustomer(customerName, addressId, active, addressString);
            }
            else{ //means we are modifying object, not creating new
                int customerId = parseInt(id);
                scheduler.Scheduler.updateCustomer(customerId, customerName, addressId, active, addressString);
            }
            
            customerIdTextBox.clear();
            customerNameTextBox.clear();
            customerActiveChkBox.setSelected(false);   
        }
        catch (IllegalArgumentException iae){
            scheduler.Scheduler.openAlert(new FXMLLoader(getClass().getResource("../view/alert.fxml")), "Illegal Input", iae.getMessage());
        }
        catch (Exception e){
                System.out.println("error");
                customerWarning.setText("Check supplied data types, ensure desired address is selected at right.");
                //System.out.println(e);
        }
        
        scheduler.Scheduler.refreshTables();
    }

    @FXML
    private void editCustomer(ActionEvent event) {
        try{
            Customer customer = (Customer) customerTable.getSelectionModel().getSelectedItem();
            //enter data to edit in text fields
            setCustomerId(customer.getCustomerId());
            setCustomerName(customer.getCustomerName());
            setActive(customer.isActive());
            setAddressSelected(customer.getAddressId());
            
            customerWarning.setText("");
        }
        catch (Exception e){
                System.out.println(e.getMessage());
                customerWarning.setText("Select Customer to edit");
                //System.out.println(e);
        }
    }
    
    @FXML
    private void deleteCustomer(ActionEvent event){
        try{
            Customer customer = (Customer) customerTable.getSelectionModel().getSelectedItem();
            //if something is selected, warn user of cascading delete
            customerWarning.setText("Are you sure? Removing customers will delete all associated appointments too.");
            confirmDeleteCustomerBtn.setVisible(true);
        }
        catch (Exception e){
                System.out.println(e.getMessage());
                customerWarning.setText("Select Customer to delete");
                //System.out.println(e);
        }
    }
    
    @FXML
    private void confirmDeleteCustomer(ActionEvent event){
        try{
            Customer customer = (Customer) customerTable.getSelectionModel().getSelectedItem();
            customerWarning.setText("");
            hideConfirmDeleteBtn();
            
            scheduler.Scheduler.deleteCustomer(customer);
        }
        catch (Exception e){
                System.out.println(e.getMessage());
                customerWarning.setText("Select Customer to delete");
                //System.out.println(e);
        }
    }

    @FXML
    private void addAddress(ActionEvent event) {
        try{
            Stage addPartStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../view/Add_edit_address.fxml"));            
            Scene addPartScene = new Scene(root);
            addPartStage.setScene(addPartScene);
            addPartStage.setTitle("Manage Addresses");
            addPartStage.show();
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }

    @FXML
    private void editAddress(ActionEvent event) {
        try{
            FXMLLoader loader;
            if (addressTable.getSelectionModel().getSelectedItem() != null){
                loader = new FXMLLoader(getClass().getResource("../view/Add_edit_address.fxml"));
            
                Parent root = loader.load();
                Add_edit_addressController controller = loader.getController();

                Address selectedAddress = (Address)addressTable.getSelectionModel().getSelectedItem();

                controller.setAddressId(selectedAddress.getAddressId());
                controller.setLine1(selectedAddress.getAddress());
                controller.setLine2(selectedAddress.getAddress2());
                controller.setPostalCode(selectedAddress.getPostalCode());
                controller.setPhone(selectedAddress.getPhone());
                controller.setCitySelected(selectedAddress.getCityId());

                Scene manageAddressScene = new Scene(root);
                Stage manageAddressStage = new Stage();
                manageAddressStage.setScene(manageAddressScene);
                manageAddressStage.setTitle("Manage Addresses");
                manageAddressStage.show();
            }
            else{
                loader = new FXMLLoader(getClass().getResource("../view/alert.fxml"));
                scheduler.Scheduler.openAlert(loader, "Edit Address Failed","No Address selected. Please select address to edit.");
            }
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }
    
    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(int customerId){
        customerIdTextBox.setText(Integer.toString(customerId));
    }
    
    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName){
        customerNameTextBox.setText(customerName);
    }
    
    /**
     * @param active the active status to set
     */
    public void setActive(boolean active){
        customerActiveChkBox.setSelected(active);
    }
    
    /**
     * @param addressId the addressId to select in addressTable
     */
    public void setAddressSelected(int addressId){
        addressTable.getSelectionModel().select(scheduler.Scheduler.getAllAddresses().stream().filter(address -> address.getAddressId() == addressId).findAny().get());
    }
    
    @Override
    public void refreshTables(){
        customerTable.refresh();
        addressTable.refresh();
    }
    
}

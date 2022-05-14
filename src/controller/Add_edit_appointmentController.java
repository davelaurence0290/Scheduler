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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class Add_edit_appointmentController extends SchedulerController implements Initializable {

    @FXML
    private Button addAppointmentCancelBtn;
    @FXML
    private Label appointmentWarning;
    @FXML
    private TextField searchCustomerTextBox;
    @FXML
    private Button newCustomerBtn;
    @FXML
    private Button editCustomerBtn;
    @FXML
    private TextField locationTextBox;
    @FXML
    private TextField appointmentTitleTextBox;
    @FXML
    private Button addAppointmentSaveBtn;
    @FXML
    private TextField appointmentIdTextBox;
    @FXML
    private TextArea descriptionTextBox;
    @FXML
    private TextField contactTextBox;
    @FXML
    private TextField typeTextBox;
    @FXML
    private TextField urlTextBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TableView customerTable;
    @FXML
    private TableColumn customerTableId;
    @FXML
    private TableColumn customerTableName;
    @FXML
    private TableColumn customerTableActive;
    @FXML
    private Spinner<Integer> startHoursSpinner;
    @FXML
    private Spinner<Integer> startMinutesSpinner;
    @FXML
    private Spinner<Integer> endHoursSpinner;
    @FXML
    private Spinner<Integer> endMinutesSpinner;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //register this object in instancemap for reference
        scheduler.Scheduler.controllerInstanceMap.put(this.getClass(),this);

        //populate tables
        customerTableId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerTableName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerTableActive.setCellValueFactory(new PropertyValueFactory<>("active"));
        customerTable.setItems(scheduler.Scheduler.getAllCustomers());
        customerTable.setPlaceholder(new Label("No Customers Found"));
        
        initSpinners();
    }    

    //intitialize spinners
    private void initSpinners(){
        startHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,LocalTime.now().getHour()+1));
        startHoursSpinner.getValueFactory().setWrapAround(true);
        
        startMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,5));
        startMinutesSpinner.getValueFactory().setWrapAround(true);
        
        endHoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,LocalTime.now().getHour()+2));
        endHoursSpinner.getValueFactory().setWrapAround(true);
        
        endMinutesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,59,0,5));
        endMinutesSpinner.getValueFactory().setWrapAround(true);
    }
    
    
    @FXML
    private void close(ActionEvent event) {
        ((Stage) addAppointmentCancelBtn.getScene().getWindow()).close();
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
            customerTable.setItems(filteredPartsTable);
        }
    }

    @FXML
    private void addCustomer(ActionEvent event) {
        try{
            Stage addPartStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../view/Add_edit_customer.fxml"));            
            Scene addPartScene = new Scene(root);
            addPartStage.setScene(addPartScene);
            addPartStage.setTitle("Manage Customers");
            addPartStage.show();
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }
    
    @FXML
    private void editCustomer(ActionEvent event) {
        try{
            FXMLLoader loader;
            if (customerTable.getSelectionModel().getSelectedItem() != null){
                loader = new FXMLLoader(getClass().getResource("../view/Add_edit_customer.fxml"));
            
                Parent root = loader.load();
                Add_edit_customerController controller = loader.getController();

                Customer selectedCustomer = (Customer)customerTable.getSelectionModel().getSelectedItem();

                controller.setCustomerId(selectedCustomer.getCustomerId());
                controller.setCustomerName(selectedCustomer.getCustomerName());
                controller.setActive(selectedCustomer.isActive());
                controller.setAddressSelected(selectedCustomer.getAddressId());

                Scene manageCustomerScene = new Scene(root);
                Stage manageCustomerStage = new Stage();
                manageCustomerStage.setScene(manageCustomerScene);
                manageCustomerStage.setTitle("Manage Customers");
                manageCustomerStage.show();
            }
            else{
                loader = new FXMLLoader(getClass().getResource("../view/alert.fxml"));
                scheduler.Scheduler.openAlert(loader, "Edit Customer Failed","No Customer selected. Please select customer to edit.");
            }
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }

    @FXML
    private void save(ActionEvent event) {
        try{
            //parse supplied data
            String id = appointmentIdTextBox.getText();
            String title = appointmentTitleTextBox.getText().toUpperCase();
            String description = descriptionTextBox.getText().toUpperCase();
            String location = locationTextBox.getText().toUpperCase();
            String contact = contactTextBox.getText().toUpperCase();
            String type = typeTextBox.getText().toUpperCase();
            String url = urlTextBox.getText().toUpperCase();
            LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(startHoursSpinner.getValue(), startMinutesSpinner.getValue()));
            LocalDateTime endDateTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.of(endHoursSpinner.getValue(), endMinutesSpinner.getValue()));
            Customer customer = (Customer)customerTable.getSelectionModel().getSelectedItem();
            int customerId = customer.getCustomerId();
            
            int error = checkValid(startDateTime, endDateTime, id);
            switch(error){
                case 0:
                    appointmentWarning.setText("");
                    break;
                case -1:
                    appointmentWarning.setText("Appointment start time must be before end time.");
                    return;
                case -2:
                    appointmentWarning.setText("Appointment is outside of business hours (8h - 17h)");
                    return;
                default:
                    appointmentWarning.setText("Appointment overlaps another with AppointmentId=" + error);
                    return;
            }
            
            if (id.equals(""))//means we are creating new object.
            {
                scheduler.Scheduler.createAppointment(customerId, scheduler.Scheduler.dbInstance.currentUser.getUserId(), title, description, location, contact, type, url, startDateTime, endDateTime);
            }
            else{ //means we are modifying object, not creating new
                int appointmentId = parseInt(id);
                scheduler.Scheduler.updateAppointment(appointmentId, customerId, title, description, location, contact, type, url, startDateTime, endDateTime);
            }
            
            appointmentIdTextBox.clear();
            appointmentTitleTextBox.clear();
            descriptionTextBox.clear();
            locationTextBox.clear();
            contactTextBox.clear();
            typeTextBox.clear();
            urlTextBox.clear();
            //reset time to dateTime to next rounded-up hour, duration 1 hour.
            LocalDateTime resetStartDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour()+1, 0));
            setStartDateTime(resetStartDateTime);
            setEndDateTime(resetStartDateTime.plusHours(1));
            scheduler.Scheduler.refreshTables();
            
            ((Stage)addAppointmentSaveBtn.getScene().getWindow()).close();
        }
        catch (IllegalArgumentException iae){
            scheduler.Scheduler.openAlert(new FXMLLoader(getClass().getResource("../view/alert.fxml")), "Illegal Input", iae.getMessage());
        }
        catch (Exception e){
            System.out.println("error\n" + e.getMessage());
            appointmentWarning.setText("Check supplied data types and ensure desired customer is selected at right.");
        }
        
        scheduler.Scheduler.refreshTables();
    }
    
    private int checkValid(LocalDateTime start, LocalDateTime end, String id){
        // set appointmentId, if existing appointment, update to actual Id
        // Id will be used later for ignoring time intersections with self
        int appointmentId = 0;
        if(! id.equals("")){
            appointmentId = Integer.parseInt(id);
        }
        final int fAppointmentId = appointmentId;
        
        //make sure start is before end
        if(start.isAfter(end) || start.equals(end)){
            return -1;
        }
        //check if appointment outside business hours
        //must be between 8 and 5
        if( start.getHour() < 8 || end.getHour() <= 8 ||
            start.getHour() >= 17 || (end.getHour() > 16 && end.getMinute() > 0)
          ){
            return -2;//outside business hours
        }
        
        
        int aptId = 0;
        ObservableList<Appointment> allAppointments = scheduler.Scheduler.getAllAppointments();
        Optional<Appointment> foundApt = allAppointments.filtered(apt ->
                (
                (start.isBefore(apt.getStart()) && end.isAfter(apt.getStart())) || //Another apt starts during this one's duration
                (start.equals(apt.getStart()) && end.isAfter(apt.getStart())) || //Another apt starts at the same time
                (start.isBefore(apt.getEnd()) && end.isAfter(apt.getEnd())) || //Another apt ends during this one's duration
                (start.isBefore(apt.getEnd()) && end.equals(apt.getEnd())) || //Another apt ends at the same time
                (start.equals(apt.getStart()) && end.equals(apt.getEnd())) || //Another apt has the same start/end.
                (start.isAfter(apt.getStart()) && end.isBefore(apt.getEnd())) //Another apt time surrounds this one.
                ) && apt.getAppointmentId() != fAppointmentId // appointment should ignore time intersections with itself
                ).stream()
                .findFirst(); 
       
        if(foundApt.isPresent()){
            aptId = foundApt.get().getAppointmentId();
        }
        return aptId;
    }
    
    /**
     * @param appointmentId the customerId to set
     */
    public void setAppointmentId(int appointmentId){
        appointmentIdTextBox.setText(Integer.toString(appointmentId));
    }
    
    /**
     * @param title the title to set
     */
    public void setTitle(String title){
        appointmentTitleTextBox.setText(title);
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(String description){
        descriptionTextBox.setText(description);
    }
    
    /**
     * @param location the location to set
     */
    public void setLocation(String location){
        locationTextBox.setText(location);
    }
    
    /**
     * @param contact the contact to set
     */
    public void setContact(String contact){
        contactTextBox.setText(contact);
    }
    
    /**
     * @param type the appointment type to set
     */
    public void setType(String type){
        typeTextBox.setText(type);
    }
    
    /**
     * @param url the url to set
     */
    public void setUrl(String url){
        urlTextBox.setText(url);
    }
    
    /**
     * @param startDateTime the StartDateTime to set
     */
    public void setStartDateTime(LocalDateTime startDateTime){
        startDatePicker.setValue(startDateTime.toLocalDate());
        startHoursSpinner.getValueFactory().setValue(startDateTime.getHour());
        startMinutesSpinner.getValueFactory().setValue(startDateTime.getMinute());
    }
    
    /**
     * @param endDateTime the EndDateTime to set
     */
    public void setEndDateTime(LocalDateTime endDateTime){
        endHoursSpinner.getValueFactory().setValue(endDateTime.getHour());
        endMinutesSpinner.getValueFactory().setValue(endDateTime.getMinute());
    }
    
    /**
     * @param customerId the customerId to select in customerTable
     */
    public void setCustomerSelected(int customerId){
        customerTable.getSelectionModel().select(scheduler.Scheduler.getAllCustomers().stream().filter(customer -> customer.getCustomerId() == customerId).findAny().get());
    }
    
    @Override
    public void refreshTables(){
        customerTable.refresh();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import scheduler.Appointment;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class MainController extends SchedulerController implements Initializable {

    @FXML
    private MenuItem mainMenuExit;
    @FXML
    private MenuItem mainMenuEditAddAppointment;
    @FXML
    private MenuItem mainMenuEditEditAppointment;
    @FXML
    private MenuItem mainMenuEditDeleteAppointment;
    @FXML
    private MenuItem mainMenuEditManageCustomers;
    @FXML
    private MenuItem mainMenuHelpAbout;
    @FXML
    private Button addAppointmentBtn;
    @FXML
    private Button editAppointmentBtn;
    @FXML
    private Button deleteAppointmentBtn;
    @FXML
    private Button backTimeStepBtn;
    @FXML
    private Button forwardTimeStepBtn;
    @FXML
    private RadioButton weekDisplayToggle;
    @FXML
    private ToggleGroup displayType;
    @FXML
    private RadioButton monthDisplayToggle;
    @FXML
    private Button mainExitBtn;
    @FXML
    private Label calendarDisplayLabel;
    @FXML
    private GridPane calGridPane;
    @FXML
    private TextArea apptDetailsView;
    
    public static LocalDate curDate = LocalDate.now().withDayOfMonth(1);;
    @FXML
    private MenuItem mainMenuGenerateReport;
    @FXML
    private Button generateReportBtn;
    
    private static Appointment selectedAppt;
    private static LocalDate lastDateClicked = LocalDate.now();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //register this object in instancemap for reference
        scheduler.Scheduler.controllerInstanceMap.put(this.getClass(),this);
                
        updateCalendarDisplay();
    }    
    //Checks for currently running or coming appointments
    public void checkForComingAppointments(){
        
        final LocalDateTime curDateTime = LocalDateTime.now();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/alert.fxml"));
        //search for apts that are currently underway
        Optional<Appointment> curApt = scheduler.Scheduler.getAllAppointments().filtered(
                apt -> (apt.getStart().isBefore(curDateTime) &&
                        curDateTime.isBefore(apt.getEnd()) &&
                        apt.getUserId() == scheduler.Scheduler.dbInstance.currentUser.getUserId())
                ).stream().findFirst();
        if(curApt.isPresent()) {
            scheduler.Scheduler.openAlert(loader, "Appointment Underway", "An appointment is currently underway:\n"
                    + curApt.get().toString());
        }
        
        
        //search for apts that are within 15 mins of starting
        Optional<Appointment> soonApt = scheduler.Scheduler.getAllAppointments().filtered(
                apt -> (curDateTime.isBefore(apt.getStart()) && 
                        curDateTime.plusMinutes(15).isAfter(apt.getStart()) && 
                        apt.getUserId() == scheduler.Scheduler.dbInstance.currentUser.getUserId())
                ).stream().findFirst();
        if(soonApt.isPresent()) {
            Appointment warnApt = soonApt.get();
            scheduler.Scheduler.openAlert(loader, "Appointment Soon", "An appointment is Starting in " + 
                    Duration.between(curDateTime, warnApt.getStart()).toMinutes() + " minutes:\n" +
                    warnApt.toString());
        }
    }
    
    @FXML
    public void updateCalendarDisplay(){
        calGridPane.getChildren().clear();
        //reset row count
        calGridPane.getRowConstraints().clear();
        
        
        if(monthDisplayToggle.isSelected()){
            //update display label message
            calendarDisplayLabel.setText(Month.of(curDate.getMonthValue()).name() + " " + curDate.getYear());
            
            //add five week rows
            for(int ii = 0; ii <5; ii++){
                calGridPane.getRowConstraints().add(new RowConstraints());
            }
            
            //colStart will be the column to start in dependent on first day of month position.
            int colStart = curDate.withDayOfMonth(1).getDayOfWeek().getValue() % 7;
            int colIndex = colStart;
            int rowIndex = 0;
            
            for(LocalDate date = curDate.withDayOfMonth(1); date.getMonth() == curDate.getMonth(); date = date.plusDays(1)){
                calGridPane.add(new DayPane(date),colIndex, rowIndex);
                colIndex = ++colIndex % 7;
                rowIndex = (date.getDayOfMonth() + colStart) / 7;
            }
        }
        else{
            //update display to show the current week's days.
            LocalDate startDate = curDate.minusDays(curDate.getDayOfWeek().getValue()%7);
            LocalDate endDate = startDate.plusDays(6);
            String displayLabel;
            //construct display label dependant on the span of days (between months? years?)
            displayLabel = Month.of(startDate.getMonthValue()).name() + " " + 
                                    startDate.getDayOfMonth() + 
                            (startDate.getYear() != endDate.getYear() ? " "+startDate.getYear() : "") +
                            " - " + 
                            (startDate.getMonthValue() != endDate.getMonthValue() ? endDate.getMonth().name()+ " " : "") +
                            endDate.getDayOfMonth() + " " +
                            (startDate.getYear() != endDate.getYear() ? endDate.getYear() : "");
            calendarDisplayLabel.setText(displayLabel);
            //add 1 week row
            calGridPane.getRowConstraints().add(new RowConstraints());
            
            int colOffset = curDate.getDayOfWeek().getValue() % 7;
            int colIndex = 0;
            
            while(colIndex < 7){
                calGridPane.add(new DayPane(curDate.minusDays(colOffset-colIndex)),colIndex, 0);
                colIndex++;
            }
        }
        
        //call to populate the updated display format.
        populateCalendarDisplay();
    }
    
    public void populateCalendarDisplay(){
        //for each pane in the grid, looks for appointments in the full list of apts, then add to the pane's listview.
        for (Node pane : calGridPane.getChildren()){
            ((DayPane)pane).appointments.setItems(
                    scheduler.Scheduler.getAllAppointments()
                            .filtered(apt -> apt.getStart().toLocalDate().equals(((DayPane)pane).getDate()) &&
                                             apt.getUserId() == scheduler.Scheduler.dbInstance.currentUser.getUserId())
            );
        }
    }
    
    public void setApptDetails(){
        String details = "Appointment Details:\n\n";
        
        if (selectedAppt != null){
            scheduler.Customer cust = scheduler.Scheduler.getAllCustomers().stream().filter(customer -> customer.getCustomerId() == selectedAppt.getCustomerId()).findAny().get();
            details += selectedAppt.getTitle() + "\n\n"
                    + selectedAppt.getDescription() + "\n\n"
                    + selectedAppt.getType() + "\n\n"
                    + cust.getCustomerName() + "\n"
                    + "Contact: " + selectedAppt.getContact() + "\n\n"
                    + selectedAppt.getLocation() + "\n\n"
                    + selectedAppt.getStart() + "-\n"
                    + selectedAppt.getEnd() + "\n";
        }
        apptDetailsView.setText(details);
    }
    
    @FXML
    private void exit(ActionEvent event) {
        ((Stage) mainExitBtn.getScene().getWindow()).close();
    }

    @FXML
    private void addAppointment(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/Add_edit_appointment.fxml"));
            
            Parent root = loader.load();
            Add_edit_appointmentController controller = loader.getController();
            
            LocalDate date = lastDateClicked;//null;

            //If date is focused, set appointment default times to noon on selected day
            if(date != null){
                LocalDateTime dateTime = date.atTime(12, 0);
                controller.setStartDateTime(dateTime);
                controller.setEndDateTime(dateTime.plusHours(1));
            }
            
            Stage addAppointmentStage = new Stage();         
            Scene addAppointmentScene = new Scene(root);
            addAppointmentStage.setScene(addAppointmentScene);
            addAppointmentStage.setTitle("Manage Appointments");
            addAppointmentStage.show();
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }

    @FXML
    private void editAppointment(ActionEvent event) {
        try{
            FXMLLoader loader;
            //find selected appointment
            Appointment selectedAppointment = null;
            for( Node pane : calGridPane.getChildren()){
                DayPane dayPane = (DayPane)pane;
                if( dayPane.hasSelected()){
                    selectedAppointment = dayPane.getSelected();
                    break;
                }
            }
            
            if (selectedAppointment != null){
                loader = new FXMLLoader(getClass().getResource("../view/Add_edit_appointment.fxml"));
            
                Parent root = loader.load();
                Add_edit_appointmentController controller = loader.getController();

                controller.setAppointmentId(selectedAppointment.getAppointmentId());
                controller.setTitle(selectedAppointment.getTitle());
                controller.setDescription(selectedAppointment.getDescription());
                controller.setLocation(selectedAppointment.getLocation());
                controller.setContact(selectedAppointment.getContact());
                controller.setType(selectedAppointment.getType());
                controller.setUrl(selectedAppointment.getUrl());
                controller.setStartDateTime(selectedAppointment.getStart());
                controller.setEndDateTime(selectedAppointment.getEnd());
                controller.setCustomerSelected(selectedAppointment.getCustomerId());

                Scene manageAppointmentScene = new Scene(root);
                Stage manageAppointmentStage = new Stage();
                manageAppointmentStage.setScene(manageAppointmentScene);
                manageAppointmentStage.setTitle("Manage Appointments");
                manageAppointmentStage.show();
            }
            else{
                loader = new FXMLLoader(getClass().getResource("../view/alert.fxml"));
                scheduler.Scheduler.openAlert(loader, "Edit Appointment Failed","No appointment selected. Please select appointment to edit.");
            }
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }

    @FXML
    private void deleteAppointment(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/alert.fxml"));
            //find selected appointment
            Appointment selectedAppointment = null;
            for( Node pane : calGridPane.getChildren()){
                DayPane dayPane = (DayPane)pane;
                if( dayPane.hasSelected()){
                    selectedAppointment = dayPane.getSelected();
                    break;
                }
            }
            
            if (selectedAppointment != null){
                
                String deleteMsg = "Appointment:\n"
                        + "[" + selectedAppointment.toString() + "]\n"
                        + "Deleted" ;
                
                scheduler.Scheduler.deleteAppointment(selectedAppointment);
                
                scheduler.Scheduler.openAlert(loader, "Appointment Deleted",deleteMsg);
            }
            else{
                scheduler.Scheduler.openAlert(loader, "Delete Appointment Failed","No appointment selected. Please select appointment to delete.");
            }
        }
        catch(Exception e){
            System.out.println("didn't work\n" + e);
        }
    }

    @FXML
    private void manageCustomers(ActionEvent event) {
        try{
            Stage manageCustomerStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../view/Add_edit_customer.fxml"));            
            Scene manageCustomerScene = new Scene(root);
            manageCustomerStage.setScene(manageCustomerScene);
            manageCustomerStage.setTitle("Manage Customers");
            manageCustomerStage.show();
        }
        catch(IOException e){
            System.out.println("didn't work\n" + e);
        }
    }
    
    @FXML
    private void aboutApp(ActionEvent event) {
        try{
            String aboutMsg = "Scheduler 1.1\n"
                    + "Authored by Dave Smith\n"
                    + "WGU C868, Spring 2021\n"
                    + "\n"
                    + "Hope you enjoy(ed) Scheduling!";
                
            FXMLLoader loader;
            loader = new FXMLLoader(getClass().getResource("../view/alert.fxml"));

            scheduler.Scheduler.openAlert(loader, "About Scheduler",aboutMsg);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void backTimeStep(ActionEvent event) {
        if(monthDisplayToggle.isSelected())
            curDate = curDate.minusMonths(1);
        else
            curDate = curDate.minusWeeks(1);
        
        updateCalendarDisplay();
    }

    @FXML
    private void forwardTimeStep(ActionEvent event) {
        if(monthDisplayToggle.isSelected())
            curDate = curDate.plusMonths(1);
        else
            curDate = curDate.plusWeeks(1);
        
        updateCalendarDisplay();
    }
    
    @FXML
    private void clearSelected(){
        //clear all selections from panes whose ListViews do not have focus.
        calGridPane.getChildren().stream().forEach( pane -> {if(!((DayPane)pane).hasFocus())((DayPane)pane).clearSelected();});
    }

    @FXML
    private void generateReport(ActionEvent event) {
        try{
            Stage manageCustomerStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("../view/reportPrompt.fxml"));            
            Scene manageCustomerScene = new Scene(root);
            manageCustomerStage.setScene(manageCustomerScene);
            manageCustomerStage.setTitle("Generate Report");
            manageCustomerStage.show();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }        
    }
    
    @Override
    public void refreshTables(){
        populateCalendarDisplay();
    }
    
    //class for panes that populate the calendar view
    private class DayPane extends AnchorPane {
        private Label dateNumber;
        private LocalDate date;
        private ListView<Appointment> appointments;
        
        public DayPane(LocalDate initDate){
            this.date = initDate;
            dateNumber = new Label(""+this.date.getDayOfMonth());
            DayPane.setTopAnchor(dateNumber, 2.0);
            DayPane.setLeftAnchor(dateNumber, 8.0);
            
            appointments = new ListView();
            DayPane.setTopAnchor(appointments, 20.0);
            DayPane.setBottomAnchor(appointments, 1.0);
            DayPane.setLeftAnchor(appointments, 1.0);
            DayPane.setRightAnchor(appointments, 1.0);
            
            this.getChildren().addAll(dateNumber, appointments);
            
            this.setStyle("-fx-border-color: black; -fx-border-width: 1;");
            this.appointments.setStyle("-fx-font-size:9;");
            this.appointments.setOnMouseClicked((MouseEvent event) -> {
                MainController.this.clearSelected();
                MainController.selectedAppt = null;
            });
            this.appointments.setOnMouseReleased((MouseEvent event) -> {
                MainController.lastDateClicked = this.getDate();
                MainController.selectedAppt = this.appointments.getSelectionModel().getSelectedItem();
                MainController.this.setApptDetails();
            });
        }
        
        public void setDate(LocalDate newDate){
            this.date = newDate;
            this.dateNumber.setText(""+this.date.getDayOfMonth());
        }
        
        public LocalDate getDate(){
            return this.date;
        }
        
        public void addAppointment(Appointment apt){
            this.appointments.getItems().add(apt);
        }
        
        public int getNumApts(){
            return this.appointments.getItems().size();
        }
        
        public boolean hasSelected(){
            for(int ii = 0; ii < this.appointments.getItems().size(); ii++){
                if(this.appointments.getSelectionModel().isSelected(ii))
                    return true;
            }
            return false;
        }
        
        public boolean hasFocus(){
            return this.appointments.isFocused();
        }
        
        public void clearSelected(){
            this.appointments.getSelectionModel().clearSelection();
        }
        
        public Appointment getSelected(){
            return this.appointments.getSelectionModel().getSelectedItem();
        }
        
        @Override
        public String toString(){
            return this.date.toString();
        }
    }
}

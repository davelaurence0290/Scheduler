/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scheduler.Appointment;
import scheduler.User;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class ReportController implements Initializable {

    @FXML
    private TableView reportTable;
    @FXML
    private Button reportCloseBtn;
    @FXML
    private Label reportTitle;
    @FXML
    private AnchorPane reportAnchorPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AnchorPane.setBottomAnchor(reportCloseBtn, 15.0);
        AnchorPane.setRightAnchor(reportCloseBtn, 15.0);
    }    

    @FXML
    private void close(ActionEvent event) {
        ((Stage)reportCloseBtn.getScene().getWindow()).close();
    }

    public void setUpNumAptTypesReport(){
        reportTitle.setText("Number of Appointments Types by Month (+/- 6 months from current Month)");
        //remove tableView, just use text area.
        reportAnchorPane.getChildren().remove(reportTable);
        
        LocalDate searchMonthDate  = LocalDate.now().minusMonths(6);
        Month searchMonth = LocalDate.now().minusMonths(6).getMonth();
        HashSet typeSet = new HashSet();
        ObservableList<Appointment> aptSet = FXCollections.observableArrayList();
        
        String resultsString = "" + searchMonthDate.getMonth() + " " + searchMonthDate.getYear() + " - " + 
                                    searchMonthDate.plusMonths(12).getMonth() + " " + searchMonthDate.plusMonths(12).getYear() + ":\n\n";
        //get all unique types in each month, append month and type count to results string.
        for(int ii = 0; ii < 13; ii++){
            aptSet = getAptsInMonth(searchMonthDate.plusMonths(ii).getMonth());
            aptSet.forEach(apt -> typeSet.add(apt.getType().toUpperCase())); //ignore capitalization
            
            resultsString += "" + String.format("%-9s", searchMonthDate.plusMonths(ii).getMonth()) + "    \t" + searchMonthDate.plusMonths(ii).getYear() + ":\t" + typeSet.size() + "\n";
            
            typeSet.clear();            
        }
        //add text area, anchor it, add text.
        TextArea results = new TextArea();
        AnchorPane.setTopAnchor(results, 50.0);
        AnchorPane.setBottomAnchor(results, 50.0);
        AnchorPane.setLeftAnchor(results, 25.0);
        AnchorPane.setRightAnchor(results, 25.0);
        reportAnchorPane.getChildren().add(results);
        
        results.setText(resultsString);
    }
    
    public void setUpUserScheduleReport(User user){
        
        AnchorPane.setTopAnchor(reportTable, 40.0);
        AnchorPane.setBottomAnchor(reportTable, 50.0);
        AnchorPane.setLeftAnchor(reportTable, 25.0);
        AnchorPane.setRightAnchor(reportTable, 25.0);
        
        reportTitle.setText("All Future Appointments for User: " + user.getUserName().toUpperCase());
        
        TableColumn appointmentId = new TableColumn("Apt ID");
        TableColumn customerId = new TableColumn("Customer ID");
        TableColumn title = new TableColumn("Title");
        TableColumn location = new TableColumn("Location");
        TableColumn contact = new TableColumn("Contact");
        TableColumn type = new TableColumn("Type");
        TableColumn start = new TableColumn("Start");
        TableColumn end = new TableColumn("End");
        
        reportTable.getColumns().addAll(appointmentId,customerId,title,location,contact,type,start,end);
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        contact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        start.setCellValueFactory(new PropertyValueFactory<>("start"));
        end.setCellValueFactory(new PropertyValueFactory<>("end"));
        
        //System.out.println("User ID: " + user.getUserId());
        //System.out.println("Found Apts: " + scheduler.Scheduler.getAllAppointments().filtered(apt -> apt.getUserId() == user.getUserId()).size());
        reportTable.setItems(scheduler.Scheduler.getAllAppointments().filtered(apt -> 
            ( apt.getUserId() == user.getUserId() && 
              LocalDate.now().compareTo(apt.getStart().toLocalDate()) <= 0 )
            )
            .sorted((apt1, apt2) -> apt1.getStart().compareTo(apt2.getStart()))
        );
        reportTable.setPlaceholder(new Label("No Appointments Found"));
    }
    
    public void setUpNumAptsPerDay(){
        reportTitle.setText("Number of Appointments Per Day in Current Week");
        //remove tableView, just use label text.
        reportAnchorPane.getChildren().remove(reportTable);
        
        TextArea results = new TextArea();
        AnchorPane.setTopAnchor(results, 50.0);
        AnchorPane.setBottomAnchor(results, 50.0);
        AnchorPane.setLeftAnchor(results, 25.0);
        AnchorPane.setRightAnchor(results, 25.0);
        reportAnchorPane.getChildren().add(results);
        
        LocalDate curDate = LocalDate.now();
        LocalDate weekStart = curDate.minusDays(curDate.getDayOfWeek().getValue()%7);
        
        String resultsString = "" + weekStart + " - " + weekStart.plusDays(6) + ":\n\n";
        
        LocalDate weekDate;
        for (int ii = 0; ii <7 ; ii++){
            weekDate = weekStart.plusDays(ii);
            resultsString += "" + String.format("%1$10s", weekDate.getDayOfWeek()) + ",\t" + 
                                  String.format("%1$10s", weekDate.getMonth().name()) + " " + 
                                  String.format("%1$2s", weekDate.getDayOfMonth()) + ":\t  ";
            resultsString += "" + numAptsOnDate(weekDate) + "\n";
        }
        
        results.setText(resultsString);
    }
    
    private int numAptsOnDate(LocalDate date){
        return scheduler.Scheduler.getAllAppointments().filtered(apt -> apt.getStart().toLocalDate().equals(date)).size();
    }
    
    private ObservableList<Appointment> getAptsInMonth(Month month){
        return scheduler.Scheduler.getAllAppointments().filtered(apt -> apt.getStart().getMonth().equals(month));
    }
    
}

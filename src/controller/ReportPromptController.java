/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import scheduler.User;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class ReportPromptController implements Initializable {

    @FXML
    private RadioButton numAptTypesToggle;
    @FXML
    private ToggleGroup reportType;
    @FXML
    private RadioButton userScheduleToggle;
    @FXML
    private RadioButton numAptsPerDayToggle;
    @FXML
    private ComboBox<User> selectUserComboBox;
    @FXML
    private Button okBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label reportPromptWarning;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectUserComboBox.setItems(scheduler.Scheduler.getAllUsers());
        
        reportType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (userScheduleToggle.isSelected()){
                selectUserComboBox.setDisable(false);
            }
            else{
                selectUserComboBox.setDisable(true);
                reportPromptWarning.setText("");
            }
            
        });
    }    

    @FXML
    private void generateReport(ActionEvent event) {
        
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/report.fxml"));
            
            Parent root = loader.load();
            ReportController controller = loader.getController();
            
            Stage reportStage = new Stage();    
            Scene reportScene = new Scene(root);
            reportStage.setScene(reportScene);
            
            //find out what kind of report is to be run, set up report window
            if(numAptTypesToggle.isSelected()){
                controller.setUpNumAptTypesReport();
            }
            else if (userScheduleToggle.isSelected()){
                //check if user selected
                if (selectUserComboBox.getValue() != null)
                {
                    User user = selectUserComboBox.getValue();
                    controller.setUpUserScheduleReport(user);
                }
                else{
                    reportPromptWarning.setText("Select User for Schedule Report");
                    return;                    
                }
            }
            else if (numAptsPerDayToggle.isSelected()){
                controller.setUpNumAptsPerDay();
            }
            else{
                reportPromptWarning.setText("Select Report Type to continue.");
                return;
            }
            reportPromptWarning.setText("");
            reportStage.setTitle("Report");
            reportStage.show();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }  
    }

    @FXML
    private void cancel(ActionEvent event) {
        ((Stage)cancelBtn.getScene().getWindow()).close();
    }
    
}

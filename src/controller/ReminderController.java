/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class ReminderController implements Initializable {

    @FXML
    private Button openDetailsButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private Label time;
    @FXML
    private Button dismissButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void openDetails(ActionEvent event) {
    }

    @FXML
    private void dismiss(ActionEvent event) {
    }
    
}

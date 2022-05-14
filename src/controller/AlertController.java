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
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class AlertController implements Initializable {

    @FXML
    private Button okButton;
    @FXML
    private Label alertText;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void close(ActionEvent event) {
        ((Stage)okButton.getScene().getWindow()).close();
    }
    
    /**
     * @param text the alert text to set.
     */
    public void setAlertText(String text) {
        alertText.setText(text);
    }
    
}

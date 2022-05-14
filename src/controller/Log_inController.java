/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.FileOutputStream;
import java.io.IOException;
import scheduler.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dave
 */
public class Log_inController implements Initializable {

    @FXML
    private Button logInButton;
    @FXML
    private Button logInCancelButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private Label logInWarning;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        logInButton.setText(rb.getString("ok"));
        logInCancelButton.setText(rb.getString("cancel"));
        
        logInWarning.setText(rb.getString("warning"));
        logInWarning.setVisible(false);
    }    

    @FXML
    private void logInEnter(ActionEvent event) {
        setCurrentUser(usernameText.getText(), passwordText.getText());
    }

    @FXML
    private void logInCancel(ActionEvent event) {
        ((Stage) logInCancelButton.getScene().getWindow()).close();
    }
    
    private void setCurrentUser(String userName, String pw){
        Optional<User> oUser = scheduler.Scheduler.getAllUsers().stream()
                        .filter((user -> user.getUserName().equals(userName) && user.getPassword().equals(pw)))
                        .findFirst();

        if (oUser.isPresent()){
            logInWarning.setVisible(false);
            scheduler.Scheduler.dbInstance.setCurrentUser(oUser.get());
            ((Stage) logInButton.getScene().getWindow()).close();
            try{
                launchMain(oUser.get().getUserName());
            }
            catch(IOException e){
                System.out.println(e.getMessage());
            }
            
        }
        else{
            logInWarning.setVisible(true);
        }
    }
    
    private void launchMain(String userName) throws IOException{
        //Write log in time,user to log file.        
        try(FileOutputStream fos = new FileOutputStream("logins.txt",true)){
            fos.write((LocalDateTime.now() + ", " + userName + "\n").getBytes());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        //start the main window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/main.fxml"));
            
        Parent root = loader.load();
        MainController controller = loader.getController();
        
        Scene mainScene = new Scene(root);
        Stage mainStage = new Stage();
        mainStage.setScene(mainScene);
        mainStage.setTitle("Scheduler");
        mainStage.show();
        
        controller.checkForComingAppointments();        
    }
}

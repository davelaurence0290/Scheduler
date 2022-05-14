/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static java.lang.Integer.parseInt;
import scheduler.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
public class Add_edit_cityController extends SchedulerController implements Initializable {

    @FXML
    private TableView countryTable;
    @FXML
    private TableView cityTable;
    @FXML
    private Button addCityDoneBtn;
    @FXML
    private Button saveCityBtn;
    @FXML
    private Button editCityBtn;
    @FXML
    private Label cityWarning;
    @FXML
    private TextField searchCountryTextBox;
    @FXML
    private TextField cityIdTextBox;
    @FXML
    private TextField cityNameTextBox;
    @FXML
    private Button addCountryBtn;
    @FXML
    private TextField searchCityTextBox;
    @FXML
    private TableColumn countryTableId;
    @FXML
    private TableColumn countryTableCountry;
    @FXML
    private TableColumn cityTableCity;
    @FXML
    private TableColumn cityTableCountry;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //register this object in instancemap for reference
        scheduler.Scheduler.controllerInstanceMap.put(this.getClass(),this);
        
        //populate tables
        
        countryTableId.setCellValueFactory(new PropertyValueFactory<>("countryId"));
        countryTableCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        countryTable.setItems(scheduler.Scheduler.getAllCountries());
        countryTable.setPlaceholder(new Label("No Countries Found"));
        
        cityTableCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        cityTableCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        cityTable.setItems(scheduler.Scheduler.getAllCities());
        cityTable.setPlaceholder(new Label("No Cities Found"));
    }    

    @FXML
    private void close(ActionEvent event) {
        ((Stage) addCityDoneBtn.getScene().getWindow()).close();
    }

    @FXML
    private void searchCountry(ActionEvent event) {
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
            countryTable.setItems(scheduler.Scheduler.getAllCountries());
        //otherwise, search
        else{
            FilteredList<Country> filteredPartsTable = new FilteredList<>(scheduler.Scheduler.getAllCountries(), country -> 
                ( country.getCountry().contains(searchQuery) ||
                  country.getCountryId() == finalSearchID) );//using lambda for predicate of FilteredList contructor
            countryTable.setItems(filteredPartsTable);
        }
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
    private void saveCity(ActionEvent event) {
        try{
            //parse supplied data
            String id = cityIdTextBox.getText();
            String city = cityNameTextBox.getText().toUpperCase();
            Country country = (Country)countryTable.getSelectionModel().getSelectedItem();
            int countryId = country.getCountryId();
            String countryName = country.getCountry();
            
            
            cityWarning.setText("");
            if (id.equals(""))//means we are creating new object.
            {
                //check if already exists
                if ( scheduler.Scheduler.getAllCities().stream().anyMatch(c -> c.getCountryId() == countryId && c.getCity().equals(city))){
                    cityWarning.setText("City already exists");
                }
                else{
                    scheduler.Scheduler.createCity(city, countryId, countryName);

                    cityIdTextBox.clear();
                    cityNameTextBox.clear();
                    scheduler.Scheduler.refreshTables();
                }
            }
            else{ //means we are modifying object, not creating new
                int cityId = parseInt(id);
                scheduler.Scheduler.updateCity(cityId, city, countryId, countryName);
                
                //update addresses that were using this cityId with new string of "CITY, COUNTRY"
                scheduler.Scheduler.getAllAddresses()
                        .filtered(address -> address.getCityId() == cityId)
                        .forEach(address -> address.setCity(city + ", " + countryName));
                
                cityIdTextBox.clear();
                cityNameTextBox.clear();
                scheduler.Scheduler.refreshTables();
            }
            
            
        }
        catch (IllegalArgumentException iae){
            scheduler.Scheduler.openAlert(new FXMLLoader(getClass().getResource("../view/alert.fxml")), "Illegal Input", iae.getMessage());
        }
        catch (Exception e){
                System.out.println("error");
                cityWarning.setText("Check supplied data types, ensure City's country is selected at right.");
                //System.out.println(e);
        }
    }

    @FXML
    private void editCity(ActionEvent event) {
        try{
            City city = (City) cityTable.getSelectionModel().getSelectedItem();
            //enter data to edit in text fields
            setCityId(city.getCityId());
            setCityName(city.getCity());
            setCountrySelected(city.getCountryId());
            
            cityWarning.setText("");
        }
        catch (Exception e){
                System.out.println(e.getMessage());
                cityWarning.setText("Select City to edit");
                //System.out.println(e);
        }
    }

    @FXML
    private void addCountry(ActionEvent event) {
        try{
            //parse supplied data
            String country = searchCountryTextBox.getText().toUpperCase();
            
            // check business logic input errors
            if( scheduler.Scheduler.getAllCountries().stream().anyMatch(c -> c.getCountry().equals(country))){
                cityWarning.setText("Country " + country +" already exists.");
            }
            else if( country.length() <= 1 ){
                cityWarning.setText("Country must be greater than 1 character.");
            }
            else{
                cityWarning.setText("");
                scheduler.Scheduler.createCountry(country);
            }
        }
        catch (Exception e){
                System.out.println("error");
                cityWarning.setText("Check supplied data type for new Country");
                //System.out.println(e);
        }
        
        scheduler.Scheduler.refreshTables();
    }
    
    /**
     * @param id the id to set
     */
    public void setCityId(int id){
        cityIdTextBox.setText(Integer.toString(id));
    }
    
    /**
     * @param city the city name to set
     */
    public void setCityName(String city){
        cityNameTextBox.setText(city);
    }
    
    /**
     * @param countryId the countryId to select in countryTable
     */
    public void setCountrySelected(int countryId){
        countryTable.getSelectionModel().select(scheduler.Scheduler.getAllCountries().stream().filter(country -> country.getCountryId() == countryId).findAny().get());
    }
    
    @Override
    public void refreshTables(){
        countryTable.refresh();
        cityTable.refresh();
    }
}

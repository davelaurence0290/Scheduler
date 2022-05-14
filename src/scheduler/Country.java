/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;
import java.lang.IllegalArgumentException;
/**
 *
 * @author Dave
 */
public class Country {
    private int countryId;
    private String country;
    
    public Country(String country){
        if(!country.equals("")) this.country = country.toUpperCase();
        else throw new IllegalArgumentException("Country must have a name.");
        
        this.countryId = Scheduler.dbInstance.insertCountry(this.country);
    }
    
    //only called  to populate list of existing countries from DB.
    public Country(int countryId, String country){
        this.countryId = countryId;
        this.country = country;
    }

    /**
     * @return the countryId
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }    
    
}

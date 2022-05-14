/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

/**
 *
 * @author Dave
 */
public class City {

    private final int cityId;
    private String city;
    private int countryId;
    private String country;
    private String asString;
    
    public City(String city, int countryId, String country){
        if(!city.equals("")) this.city = city.toUpperCase();
        else throw new IllegalArgumentException("City must have a name.");
        
        //entered from GUI list, assume no invalid choices.
        this.countryId = countryId;
        this.country = country.toUpperCase();
        setString();
        
        this.cityId = Scheduler.dbInstance.insertCity(this.city, this.countryId);
    }
    
    //only called  to populate list of existing cities from DB.
    public City(int cityId, String city, int countryId, String country){
        this.cityId = cityId;
        this.city = city;
        this.countryId = countryId;
        this.country = country;
        setString();
    }

    /**
     * @return the cityId
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        if(!city.equals("")) this.city = city.toUpperCase();
        else throw new IllegalArgumentException("City must have a name.");
        setString();
    }

    /**
     * @return the String equivalent of City obj
     */
    public String getAsString() {
        return this.asString;
    }

    /**
     * @return the countryId
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * @param countryId the countryId to set
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
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
        setString();
    }
    
    private void setString(){
        this.asString = this.city + ", " + this.country;
    }
}

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
public class Address {
    private final int addressId;
    private String address;
    private String address2;
    private int cityId;
    private String postalCode;
    private String phone;
    private String city;
    private String asString;
    
    public Address(String address, String address2, int cityId, String postalCode, String phone, String city){
        if(!address.equals("")) this.address = address.toUpperCase();
        else throw new IllegalArgumentException("First address line must not be empty.");
        
        this.address2 = address2.length()> 0 ? address2.toUpperCase() : "N/A"; //don't care if empty or not
        this.cityId = cityId;
        this.city = city.toUpperCase();
        
        if(!postalCode.equals("") &&
                ( postalCode.matches("\\d{5}") ||
                  postalCode.matches("\\d{5}-\\d{4}"))
          ) this.postalCode = postalCode.toUpperCase();
        else throw new IllegalArgumentException("Postal Code must of form '55555' or '55555-5555'.");
        
        //this.phone = phone.length()> 0 ? phone : "N/A";
        if(!phone.equals("") && ! phone.matches("[A-Za-z]") ) this.phone = phone;
        else throw new IllegalArgumentException("Phone must not contain alphabet characters.");
        setString();
         
        this.addressId = Scheduler.dbInstance.insertAddress(this.address, this.address2, this.cityId, this.postalCode, this.phone);
    }
    
    //only called  to populate list of existing addresses from DB.
    public Address(int addressId, String address, String address2, int cityId, String postalCode, String phone, String city){
        this.addressId = addressId;
        this.address = address;
        this.address2 = address2;
        this.cityId = cityId;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
        setString();
    }

    /**
     * @return the addressId
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        if(!address.equals("")) this.address = address.toUpperCase();
        else throw new IllegalArgumentException("First address line must not be empty.");
        setString();
    }

    /**
     * @return the address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * @param address2 the address2 to set
     */
    public void setAddress2(String address2) {
        this.address2 = address2.toUpperCase();
        setString();
    }

    /**
     * @return the cityId
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * @param cityId the cityId to set
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
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
        this.city = city.toUpperCase();
        setString();
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        if(!postalCode.equals("") &&
                ( postalCode.matches("\\d{5}") ||
                  postalCode.matches("\\d{5}-\\d{4}"))
          ) this.postalCode = postalCode.toUpperCase();
        else throw new IllegalArgumentException("Postal Code must of form '55555' or '55555-5555'.");
        setString();
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        if(!phone.equals("") && phone.matches("[^A-Za-z]+") ) this.phone = phone;
        else throw new IllegalArgumentException("Phone must not be empty nor contain alphabet characters.");
        setString();
    }

    /**
     * @return the asString
     */
    public String getAsString() {
        return asString;
    }

    private void setString() {
        this.asString = this.address + "\n" +
                        (this.address2.equals("N/A") ? "" : this.address2 + "\n") +
                        this.city + ( this.postalCode.equals("N/A") ? "" : ", " + this.postalCode + "\n") +
                        (this.phone.equals("N/A") ? "" : this.phone);
    }
    
}

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
public class Customer {
    private final int customerId;
    private String customerName;
    private int addressId;
    private boolean active;
    private String address;
    
    public Customer(String customerName, int addressId, boolean active, String address){
        if(!customerName.equals("")) this.customerName = customerName.toUpperCase();
        else throw new IllegalArgumentException("Customer must have a name.");
        
        //entered from GUI list, assume no invalid choices.
        this.addressId = addressId;
        this.active = active;
        this.address = address.toUpperCase();
        
        this.customerId = Scheduler.dbInstance.insertCustomer(this.customerName, this.addressId, this.active);
    }
    
    //only called  to populate list of existing customers from DB.
    public Customer(int customerId, String customerName, int addressId, int active, String address){
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        this.active = (active == 1);
        this.address = address;
    }

    /**
     * @return the customerId
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        if(!customerName.equals("")) this.customerName = customerName.toUpperCase();
        else throw new IllegalArgumentException("Customer must have a name.");
    }

    /**
     * @return the addressId
     */
    public int getAddressId() {
        return addressId;
    }

    /**
     * @param addressId the addressId to set
     */
    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
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
        this.address = address.toUpperCase();
    }
    
    /**
     * @return the description of Customer
     */
    public String toString() {
        return this.customerName + "\n" + this.address;
    }
}

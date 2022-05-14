/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.time.LocalDateTime;

/**
 *
 * @author Dave
 */
public class Appointment {
    private final int appointmentId;
    private int customerId;
    private int userId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private LocalDateTime start;
    private LocalDateTime end;
    private String asString;
    
    public Appointment(int customerId, int userId, String title, 
            String description, String location, String contact, String type, 
            String url, LocalDateTime start, LocalDateTime end){
        this.customerId = customerId;
        this.userId = userId;
                
        if(!title.equals("")) this.title = title;
        else throw new IllegalArgumentException("Title cannot be empty.");
        
        this.description = description.length()> 0 ? description : "N/A";
        
        if(!location.equals("")) this.location = location;
        else throw new IllegalArgumentException("Location cannot be empty.");
        
        this.contact = contact.length()>0? contact : "N/A";
        
        if(!type.equals("")) this.type = type;
        else throw new IllegalArgumentException("Type cannot be empty.");
        
        this.url = url.length()> 0 ? url : "N/A";
        this.start = start;
        this.end = end;
        
        this.appointmentId = Scheduler.dbInstance.insertAppointment(this.customerId, this.userId,
                this.title, this.description, this.location, this.contact,
                this.type, this.url, this.start, this.end);
    }
    
    //only called  to populate list of existing appointments from DB.
    public Appointment(int appointmentId, int customerId, int userId,
            String title, String description, String location, String contact,
            String type, String url, LocalDateTime start, LocalDateTime end){
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.start = start;
        this.end = end;
    }

    /**
     * @return the appointmentId
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * @return the customerId
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        if(!title.equals("")) this.title = title;
        else throw new IllegalArgumentException("Title cannot be empty.");
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        if(!location.equals("")) this.location = location;
        else throw new IllegalArgumentException("Location cannot be empty.");
    }

    /**
     * @return the contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        if(!type.equals("")) this.type = type;
        else throw new IllegalArgumentException("Type cannot be empty.");
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the start
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
    
    /**
     * @return the description of Appointment
     */
    public String toString() {
        return this.title + "\n" +
                this.location + ", " + this.start.toLocalTime();
    }
}

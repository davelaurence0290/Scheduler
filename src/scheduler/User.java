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
public class User {
    private final int userId;
    private String userName;
    private String password;
    private boolean active;
    
    public User(String userName, String password, boolean active){
        if(!userName.equals("")) this.userName = userName;
        else throw new IllegalArgumentException("User must have a name.");
        
        if(!password.equals("")) this.password = password;
        else throw new IllegalArgumentException("Password cannot be empty.");
        
        //entered from GUI list, assume no invalid choices.
        this.active = active;
        
        this.userId = Scheduler.dbInstance.insertUser(this.userName, this.password, this.active);
    }
    
    //only called  to populate list of existing users from DB.
    public User(int userId, String userName, String password, int active){
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.active = (active == 1);
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
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
    
    @Override
    public String toString(){
        return this.userName.toUpperCase();
    }
}

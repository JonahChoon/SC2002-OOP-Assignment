package sc2002;

public abstract class User {
    protected String userID;
    protected String name;
    protected String role;

    // Constructor
    public User(String userID, String name, String role) {
        this.userID = userID;
        this.name = name;
        this.role = role;
    }

    // Method to log in
    public void login() {
        System.out.println(name + " logged in as " + role);
    }

    // Method to log out
    public void logout() {
        System.out.println(name + " logged out.");
    }

    // Abstract method to be implemented by subclasses
    public abstract void accessPortal();

    // Getter for userID
    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}

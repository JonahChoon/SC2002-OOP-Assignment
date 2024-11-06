package sc2002;

/**
 * Represents a general user in the hospital management system. This class
 * serves as a base for different types of users (e.g., Patient, Doctor).
 */
public abstract class User {
	protected String userID;
	protected String name;
	protected String role;

	/**
	 * Constructs a User object with the specified ID, name, and role.
	 *
	 * @param userID The unique ID of the user.
	 * @param name   The name of the user.
	 * @param role   The role of the user (e.g., "Patient", "Doctor",
	 *               "Administrator").
	 */
	public User(String userID, String name, String role) {
		this.userID = userID;
		this.name = name;
		this.role = role;
	}

	/**
	 * Simulates the login process for the user. Outputs a message indicating that
	 * the user has logged in.
	 */
	public void login() {
		System.out.println(name + " logged in as " + role);
	}

	/**
	 * Simulates the logout process for the user. Outputs a message indicating that
	 * the user has logged out.
	 */
	public void logout() {
		System.out.println(name + " logged out.");
	}

	/**
	 * Provides an abstract method for accessing the user-specific portal.
	 * Implementations should handle the portal-specific logic for each user type.
	 */
	public abstract void accessPortal();

	/**
	 * Retrieves the user ID.
	 *
	 * @return The user ID.
	 */
	public String getUserID() {
		return this.userID;
	}

	/**
	 * Sets the user ID.
	 *
	 * @param userID The new user ID.
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * Retrieves the name of the user.
	 *
	 * @return The name of the user.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the user.
	 *
	 * @param name The new name of the user.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the role of the user.
	 *
	 * @return The role of the user.
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role of the user.
	 *
	 * @param role The new role of the user.
	 */
	public void setRole(String role) {
		this.role = role;
	}

}

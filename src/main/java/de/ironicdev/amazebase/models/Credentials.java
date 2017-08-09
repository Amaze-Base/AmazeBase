package de.ironicdev.amazebase.models;

/**
 * Created by marco on 12.07.17.
 */
public class Credentials {
    private String username;
    private String password;
    private String clientId;

    /**
     * default constructor
     */
    public Credentials() {
    }

    /**
     * Constructor with pre-defined params
     *
     * @param username for login
     * @param password for login
     */
    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

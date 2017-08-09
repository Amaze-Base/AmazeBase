package de.ironicdev.amazebase.models;

import com.mongodb.DBObject;

/**
 * Created by marco on 23.07.17.
 */
public class Client {
    private String clientName;
    private String clientKey;

    public Client() {
    }

    public Client(DBObject object){

    }

    public Client(String name) {
        this.clientName = name;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }
}

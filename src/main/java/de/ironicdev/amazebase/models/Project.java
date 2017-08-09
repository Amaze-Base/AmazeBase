package de.ironicdev.amazebase.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 23.07.17.
 */
public class Project {
    private String projectId;

    private String projectName;
    private String apiKey;
    private List<Client> clientList = new ArrayList<>();

    @JsonIgnore
    private transient MongoOperations database;

    public Project() {
    }

    public Project(String name) {
        this.projectName = name;
    }

    public Project(DBObject dbObject){
        projectId = dbObject.get("_id").toString();
        projectName = dbObject.get("projectName").toString();
        apiKey = dbObject.get("apiKey").toString();


    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public MongoOperations getDatabase() {
        return database;
    }

    public void setDatabase(MongoOperations database) {
        this.database = database;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public boolean validateClient(String clientKey){
        return clientList.stream().anyMatch(client -> client.getClientKey().equals(clientKey));
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

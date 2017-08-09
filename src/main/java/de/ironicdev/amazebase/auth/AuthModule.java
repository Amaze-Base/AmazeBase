package de.ironicdev.amazebase.auth;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import de.ironicdev.amazebase.models.Project;
import de.ironicdev.amazebase.models.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthModule {

    private final String AUTH_COLLECTION = "authentication";
    @Autowired
    TokenValidation validation;

    public String authenticateUser(Credentials credentials, Project project) throws Exception {
        DBCollection collection = project.getDatabase().getCollection(AUTH_COLLECTION);

        BasicDBList andQuery = new BasicDBList();
        andQuery.add(new BasicDBObject("username", credentials.getUsername()));
        andQuery.add(new BasicDBObject("password", credentials.getPassword()));

        DBObject user = collection.findOne(new BasicDBObject("$and", andQuery));
        if (user != null)
            return validation.createToken();
        else
            throw new Exception("invalid credentials");
    }

    public void registerUser(String username, String password, Project project) {
        DBCollection collection = project.getDatabase().getCollection(AUTH_COLLECTION);

        JsonObject jsonUsr = new JsonObject();
        jsonUsr.addProperty("username", username);
        jsonUsr.addProperty("password", password);
        collection.save((DBObject) JSON.parse(jsonUsr.toString()));
    }

}

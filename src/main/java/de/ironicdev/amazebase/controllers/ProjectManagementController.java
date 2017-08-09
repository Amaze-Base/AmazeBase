package de.ironicdev.amazebase.controllers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import de.ironicdev.amazebase.SystemData;
import de.ironicdev.amazebase.models.Project;
import de.ironicdev.amazebase.utils.BasicUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
public class ProjectManagementController {

    private SystemData systemData = SystemData.getInstance();

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public List<Project> getProjects() {
        return systemData.getProjectList();
    }

    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.GET)
    public Project getSingleProject(@PathVariable("projectId") String projectId) {
        return systemData.getProjectByKey(projectId);
    }

    @RequestMapping(value = "/projects", method = RequestMethod.PUT)
    public Project updateProject(@RequestBody Project project) {
        MongoOperations mongo = systemData.getProjectByName("_system").getDatabase();
        DBCollection collection = mongo.getCollection("projects");

        // query to find project in db
        BasicDBObject findQuery = new BasicDBObject("_id", new ObjectId(project.getProjectId()));

        // check if project exists, before update
        DBObject dbProj = collection.findOne(findQuery);

        if (dbProj == null) return null;

        // update project in database
        DBObject obj = (DBObject) JSON.parse(project.toString());
        collection.update(findQuery, obj);

        return project;
    }

    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    public Project createProject(@RequestBody Project project) {
        MongoOperations mongo = systemData.getProjectByName("_system").getDatabase();
        DBCollection collection = mongo.getCollection("projects");

        // check if project with this name already exists. only unique project names are allowed
        DBObject existingProject = collection.findOne(new BasicDBObject("projectName", project.getProjectName()));

        if (existingProject != null) return null;

        /* calc new SHA-256 key for project to get an unique key for validation */
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    project.getProjectName().getBytes(StandardCharsets.UTF_8));

            String hexHash = BasicUtils.bytesToHex(encodedHash);
            project.setApiKey(hexHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // add project to database
        DBObject obj = (DBObject) JSON.parse(project.toString());
        collection.save(obj);

        project.setProjectId(obj.get("_id").toString());

        return project;
    }

    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.DELETE)
    public void deleteProject(@PathVariable("projectId") String projectId) {
        MongoOperations mongo = systemData.getProjectByName("_system").getDatabase();
        DBCollection collection = mongo.getCollection("projects");

        BasicDBObject findQuery = new BasicDBObject("_id", new ObjectId(projectId));
        DBObject dbProject = collection.findOne(findQuery);

        if (dbProject == null) return; // TODO: Fehler-Meldung

        collection.remove(findQuery);
    }
}

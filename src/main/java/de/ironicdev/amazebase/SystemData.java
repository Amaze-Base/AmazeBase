package de.ironicdev.amazebase;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import de.ironicdev.amazebase.models.Project;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SystemData {
    private static SystemData instance;

    public static SystemData getInstance() {
        if (instance == null) instance = new SystemData();
        return instance;
    }

    // Class definition
    private List<Project> projectList = new ArrayList<>();

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public Project getProjectByKey(String apiKey) {
        return projectList.stream().filter(item -> item.getApiKey().equals(apiKey)).findFirst().get();
    }

    public Project getProjectByName(String name) {
        return projectList.stream().filter(item -> item.getProjectName().equals(name)).findFirst().get();
    }

    public void loadProjects() {
        projectList.clear();

        try {
            MongoOperations mongo = new MongoConfig().mongoTemplate("_system");
            DBCollection collection = mongo.getCollection("projects");

            DBCursor cursor = collection.find();
            while (cursor.hasNext()) {
                DBObject dbProj = cursor.next();
                Project project = new Project(dbProj);

                projectList.add(project);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

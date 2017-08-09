package de.ironicdev.amazebase;

import com.mongodb.DBCollection;
import de.ironicdev.amazebase.models.Project;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoOperations;

@SpringBootApplication
public class IcebaseApplication {


    public static void main(String[] args) {
        // init projects
        SystemData systemData = SystemData.getInstance();
        //systemData.setProjectList(MockData.initMockData());

        for (Project PROJECT : systemData.getProjectList()) {
            MongoOperations mongo = null;
            try {
                mongo = new MongoConfig().mongoTemplate(PROJECT.getProjectName());

                DBCollection coll = mongo.getCollection("data");
                coll.createIndex("_prnt");
                coll.createIndex("_node");

                PROJECT.setDatabase(mongo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* Add project for system paramters and config */
        try {
            Project systemProject = new Project("_system");
            MongoOperations mongo = new MongoConfig().mongoTemplate(systemProject.getProjectName());
            systemProject.setDatabase(mongo);
            systemData.getProjectList().add(systemProject);
        } catch (Exception e) {
        }

        SpringApplication.run(IcebaseApplication.class, args);
    }
}

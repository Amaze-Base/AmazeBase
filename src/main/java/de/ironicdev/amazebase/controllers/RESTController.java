package de.ironicdev.amazebase.controllers;

import com.google.gson.JsonObject;
import com.mongodb.*;
import com.mongodb.util.JSON;
import de.ironicdev.amazebase.MongoConfig;
import de.ironicdev.amazebase.SystemData;
import de.ironicdev.amazebase.models.Node;
import de.ironicdev.amazebase.models.Project;
import de.ironicdev.amazebase.models.Snapshot;
import de.ironicdev.amazebase.models.TransportSnapshot;
import de.ironicdev.amazebase.utils.NodeUtils;
import de.ironicdev.amazebase.utils.SnapshotUtils;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RESTController {

    private final String DATA_COLLECTION = "data";

    private ApplicationContext ctx =
            new AnnotationConfigApplicationContext(MongoConfig.class);
    private MongoOperations mongo = (MongoOperations) ctx.getBean("mongoTemplate");
    private DBCollection systemCollection = mongo.getCollection("data");

    private SystemData systemData = SystemData.getInstance();

    @RequestMapping(value = "/rest/**", method = RequestMethod.GET, produces = "application/json")
    public String getValue(@RequestParam("clientId") String clientId,
                           @RequestParam("apiKey") String apiKey,
                             HttpServletRequest request) {

        String nodePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        Project proj = systemData.getProjectByKey(apiKey); // check if project exists
        DBCollection collection = proj.getDatabase().getCollection(DATA_COLLECTION);

        TransportSnapshot snapshot = new TransportSnapshot();
        snapshot.setClientKey(clientId);
        snapshot.setApiKey(apiKey);
        snapshot.setPath(nodePath.substring(5)); // start after "/rest"

        Snapshot snap = NodeUtils.select(collection, snapshot);
        if(snap != null)

            return SnapshotUtils.toJson(snap).toString();
        else
            return "";
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.POST)
    public void setValue(HttpServletRequest request,
                         @RequestParam("clientId") String clientId,
                         @RequestParam("apiKey") String apiKey,
                         @RequestBody String val) {

        String nodePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        Project proj = systemData.getProjectByKey(apiKey); // check if project exists
        DBCollection collection = proj.getDatabase().getCollection(DATA_COLLECTION);

        TransportSnapshot snapshot = new TransportSnapshot();
        snapshot.setData(val);
        snapshot.setClientKey(clientId);
        snapshot.setApiKey(apiKey);
        snapshot.setPath(nodePath.substring(5)); // start after "/rest"


        NodeUtils.push(collection, snapshot);
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.PUT)
    public void updateValue(HttpServletRequest request,
                            @RequestParam("clientId") String clientId,
                            @RequestParam("apiKey") String apiKey,
                            @RequestBody String val) {

        String nodePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        Project proj = systemData.getProjectByKey(apiKey); // check if project exists
        DBCollection collection = proj.getDatabase().getCollection(DATA_COLLECTION);

        TransportSnapshot snapshot = new TransportSnapshot();
        snapshot.setData(val);
        snapshot.setClientKey(clientId);
        snapshot.setApiKey(apiKey);
        snapshot.setPath(nodePath.substring(5)); // start after "/rest"

        NodeUtils.update(collection, snapshot);
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.DELETE)
    public void deleteValue(HttpServletRequest request,
                            @RequestParam("clientId") String clientId,
                            @RequestParam("apiKey") String apiKey) {
        String nodePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        Project proj = systemData.getProjectByKey(apiKey); // check if project exists
        DBCollection collection = proj.getDatabase().getCollection(DATA_COLLECTION);

        TransportSnapshot snapshot = new TransportSnapshot();
//        snapshot.setData(val);
        snapshot.setClientKey(clientId);
        snapshot.setApiKey(apiKey);
        snapshot.setPath(nodePath.substring(5)); // start after "/rest"

        NodeUtils.remove(collection, snapshot);
    }

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public void test(@RequestParam("clientId") String clientId,
                     @RequestParam("apiKey") String apiKey,
                     @RequestParam("id") String val){
        Project proj = systemData.getProjectByKey(apiKey); // check if project exists
        DBCollection collection = proj.getDatabase().getCollection(DATA_COLLECTION);


        BasicDBList orList = new BasicDBList();
        orList.add(new BasicDBObject("_node", val));
        try {
            orList.add(new BasicDBObject("_id", new ObjectId(val)));
        } catch (Exception ex) {
        }

//        orList.add(new BasicDBObject("_nodeId", id));

        BasicDBObject orQuery = new BasicDBObject("$or", orList);

        BasicDBList andQuery = new BasicDBList();
        andQuery.add(orQuery);
        //andQuery.add(new BasicDBObject("_prnt", parentId));

        DBObject obj = collection.findOne(new BasicDBObject("$and", andQuery));
        String s = "";
    }
}

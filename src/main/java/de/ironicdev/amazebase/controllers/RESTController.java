package de.ironicdev.amazebase.controllers;

import com.google.gson.JsonObject;
import com.mongodb.*;
import com.mongodb.util.JSON;
import de.ironicdev.amazebase.MongoConfig;
import de.ironicdev.amazebase.models.Node;
import de.ironicdev.amazebase.utils.NodeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RESTController {

    private ApplicationContext ctx =
            new AnnotationConfigApplicationContext(MongoConfig.class);
    private MongoOperations mongo = (MongoOperations) ctx.getBean("mongoTemplate");
    private DBCollection collection = mongo.getCollection("data");

    @RequestMapping(value = "/rest/**", method = RequestMethod.POST)
    public void SetValue(HttpServletRequest request, @RequestBody String val) {
        String nodePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        /* parse path */
        String[] nodes = nodePath.split("/");
        String docId = "";
        for (String NODE : nodes) {
            if (NODE.equals("")) continue; // only add correct nodes

            Node n = new Node();
            n.set_node(NODE);

            /* check if node already exists in db,
            * or if some id is given and search for id*/
            Node existingNode = NodeUtils.find(NODE, docId, collection);

            if (existingNode != null) {
                /* if node exists, set docId for next node */
                docId = existingNode.get_nodeId();
            } else {
                /* insert new node */
                if (!docId.equals("")) n.set_prnt(docId);

                DBObject dbNode = (DBObject) JSON.parse(n.toString());
                collection.save(dbNode);
                docId = dbNode.get("_id").toString();
            }
        }

        /* insert the desired value at end of tree */
        DBObject obj = (DBObject) JSON.parse(val);
        obj.put("_prnt", docId);
        collection.save(obj);
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.GET, produces = "application/json")
    public String GetValue(HttpServletRequest request) {
        String nodePath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String[] nodes = nodePath.split("/");

        String parentId = "";
        Node lastNode = null;
        /* check if nodes existing in db */
        for (int i = 1; i < nodes.length; i++) {
            Node n = NodeUtils.find(nodes[i], parentId, collection);
            if (n != null) {
                parentId = n.get_nodeId();
                lastNode = n;
            }
        }

        JsonObject o = null;
        if (lastNode != null);
//            o = NodeUtils.print(lastNode, collection);

        if (o != null) return o.toString();
        else return null;
    }

    @RequestMapping(value = "/print", method = RequestMethod.GET)
    public String printAll() {
        return "";
    }





}

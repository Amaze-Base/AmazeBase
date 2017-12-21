package de.ironicdev.amazebase.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.util.JSON;
import de.ironicdev.amazebase.models.Node;
import de.ironicdev.amazebase.models.Snapshot;
import de.ironicdev.amazebase.models.TransportSnapshot;
import org.bson.types.ObjectId;

import java.util.UUID;

public class NodeUtils {
    /***
     * prints the node value and search recursive
     * for child nodes to append them
     * @param node
     * @param collection
     * @return
     */
    public static Snapshot print(Node node, DBCollection collection) {
        Snapshot retVal = null;
        JsonObject jsonObject;
        DBObject dbNode = null;
        try {
            dbNode = collection.findOne(new BasicDBObject("_id", new ObjectId(node.get_nodeId())));
        } catch (Exception ex) {
        }

        if (dbNode != null) {
            retVal = new Snapshot();
            jsonObject = new JsonParser().parse(dbNode.toString()).getAsJsonObject();

            retVal.setKey(dbNode.get("_id").toString());

            jsonObject.remove("_prnt");
            jsonObject.remove("_id");
            jsonObject.remove("_node");
            jsonObject.remove("nodeId");

            retVal.setData(jsonObject.toString());

            retVal.setNodeName(node.get_node());

            DBCursor childs = collection.find(new BasicDBObject("_prnt", node.get_nodeId()));
            while (childs.hasNext()) {
                DBObject o = childs.next();
                Node child = new Node(o);
                Snapshot prop = print(child, collection);
                String nodeName = "";
                if (child.get_node() != null)
                    nodeName = child.get_node();
//                else
//                    nodeName = child.get_nodeId();

                prop.setNodeName(nodeName);

                retVal.getChildren().add(prop);

            }
        }

        return retVal;
    }

    public static Snapshot select(DBCollection collection, TransportSnapshot snapshot) {
        String[] nodes = snapshot.getPath().split("/");

        String parentId = "";
        Node lastNode = null;
        /* check if nodes existing in db */
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].equals("")) continue; // avoid empty nodes

            Node n = NodeUtils.find(nodes[i], parentId, collection);
            if (n != null) {
                parentId = n.get_nodeId();
                lastNode = n;
            } else {
                return null; // exit method if node is not in db (path is incorrect)
            }
        }

        Snapshot snap = null;
        if (lastNode != null)
            snap = NodeUtils.print(lastNode, collection);

        if (snap != null) return snap;
        else return null;
    }

    public static void push(DBCollection collection, TransportSnapshot snapshot) {
        JsonObject obj = new JsonParser().parse(snapshot.getData()).getAsJsonObject();
        obj.addProperty("_nodeId", UUID.randomUUID().toString());

        snapshot.setData(obj.toString());

        insert(collection, snapshot);
    }

    public static void insert(DBCollection collection, TransportSnapshot snapshot) {

        String nodePath = snapshot.getPath();
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

                /* remove unnecessary data */
                dbNode.removeField("_nodeId");
                collection.save(dbNode);
                docId = dbNode.get("_id").toString();
            }
        }

        /* insert the desired value at end of tree */
        DBObject obj = (DBObject) JSON.parse(snapshot.getData());
        obj.put("_prnt", docId);
        collection.save(obj);

    }

    public static Node find(String id, String parentId, DBCollection collection) {
        Node node = null;

        BasicDBList orList = new BasicDBList();
        orList.add(new BasicDBObject("_node", id));
        try {
            orList.add(new BasicDBObject("_d", new ObjectId(id)));
        } catch (Exception ex) {
        }

        orList.add(new BasicDBObject("_nodeId", id));

        BasicDBObject orQuery = new BasicDBObject("$or", orList);

        BasicDBList andQuery = new BasicDBList();
        andQuery.add(orQuery);
        andQuery.add(new BasicDBObject("_prnt", parentId));

        DBObject obj = collection.findOne(new BasicDBObject("$and", andQuery));

        if (obj != null) {
            node = new Node(obj);
        }

        return node;
    }

    private static void removeNodes(DBCollection collection, String nodeId, boolean isChild) {
        String prntId = "";

        BasicDBObject findQry = null;
        if (!isChild)
            findQry = new BasicDBObject(new BasicDBObject(("_id"), new ObjectId(nodeId)));
        else
            findQry = new BasicDBObject(new BasicDBObject(("_prnt"), nodeId));

        // get all with id
        DBCursor foundObjs = collection.find(findQry);

        while (foundObjs.hasNext()) {
            DBObject current = foundObjs.next();

            WriteResult result = collection.remove(findQry);
            if (result.wasAcknowledged()) {
                // remove all child objects
                removeNodes(collection, current.get("_id").toString(), true);
            }
        }
    }

    public static void remove(DBCollection collection, TransportSnapshot snapshot) {
        Snapshot existingSnapshot = select(collection, snapshot);

        if (existingSnapshot != null) {
            // delete recursive nodes
            removeNodes(collection, existingSnapshot.getKey(), false);
        }
    }

    public static void update(DBCollection collection, TransportSnapshot snapshot) {
        // try to find existing node
        Snapshot existingSnapshot = select(collection, snapshot);

        // found node
        if (existingSnapshot != null) {
            BasicDBObject findQry = new BasicDBObject(("_id"), new ObjectId(existingSnapshot.getKey()));

            DBObject oldObj = collection.findOne(findQry);
            if (oldObj != null) {
                DBObject newObj = (DBObject) JSON.parse(snapshot.getData());
                newObj.put("_prnt", oldObj.get("_prnt"));
                collection.update(findQry, newObj);
            }
        }else
        {
            insert(collection, snapshot);
        }
    }
}

package de.ironicdev.amazebase.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.util.JSON;
import de.ironicdev.amazebase.models.Node;
import de.ironicdev.amazebase.models.Snapshot;
import de.ironicdev.amazebase.models.TransportSnapshot;
import org.bson.types.ObjectId;

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

            DBCursor childs = collection.find(new BasicDBObject("_prnt", node.get_nodeId()));
            while (childs.hasNext()) {
                DBObject o = childs.next();
                Node child = new Node(o);
                Snapshot prop = print(child, collection);
                String nodeName = "";
                if (child.get_node() != null)
                    nodeName = child.get_node();
                else
                    nodeName = child.get_nodeId();

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
        for (int i = 1; i < nodes.length; i++) {
            Node n = NodeUtils.find(nodes[i], parentId, collection);
            if (n != null) {
                parentId = n.get_nodeId();
                lastNode = n;
            }
        }

        Snapshot snap = null;
        if (lastNode != null)
            snap = NodeUtils.print(lastNode, collection);

        if (snap != null) return snap;
        else return null;
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
            orList.add(new BasicDBObject("_id", new ObjectId(id)));
        } catch (Exception ex) {
        }
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
}

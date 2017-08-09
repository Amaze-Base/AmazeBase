package de.ironicdev.amazebase.models;

import com.google.gson.Gson;
import com.mongodb.DBObject;

public class Node {
    private String _nodeId;
    private String _node;
    private String _prnt;

    public Node() {
        _nodeId = "";
        _node = "";
        _prnt = "";
    }

    public Node(DBObject dbObject) {
        try {
            _nodeId = dbObject.get("_id").toString();
            _prnt = dbObject.get("_prnt").toString();
            _node = dbObject.get("_node").toString();
        } catch (Exception ex) {
        }
    }

    public String get_node() {
        return _node;
    }

    public void set_node(String _node) {
        this._node = _node;
    }

    public String get_prnt() {
        return _prnt;
    }

    public void set_prnt(String _prnt) {
        this._prnt = _prnt;
    }

    public String get_nodeId() {
        return _nodeId;
    }

    public void set_nodeId(String _nodeId) {
        this._nodeId = _nodeId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

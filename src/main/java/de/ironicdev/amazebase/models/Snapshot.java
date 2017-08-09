package de.ironicdev.amazebase.models;

import java.util.ArrayList;
import java.util.List;

public class Snapshot {
    private String key;
    private List<Snapshot> children;
    private String data;

    public Snapshot(){
        children = new ArrayList<>();
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Snapshot> getChildren() {
        return children;
    }

    public void setChildren(List<Snapshot> childList) {
        this.children = childList;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

package de.ironicdev.amazebase.controllers;

import com.mongodb.DBCollection;
import de.ironicdev.amazebase.models.Project;
import de.ironicdev.amazebase.SystemData;
import de.ironicdev.amazebase.auth.AuthModule;
import de.ironicdev.amazebase.auth.TokenValidation;
import de.ironicdev.amazebase.models.Snapshot;
import de.ironicdev.amazebase.models.TransportSnapshot;
import de.ironicdev.amazebase.utils.NodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {
    private final String DATA_COLLECTION = "data";

    private SimpMessagingTemplate msgTemplate;

    @Autowired
    public SocketController(SimpMessagingTemplate template) {
        this.msgTemplate = template;
    }

    @Autowired
    private TokenValidation validation;

    @Autowired
    private AuthModule authModule;

    private SystemData systemData = SystemData.getInstance();

    @MessageMapping("/data")
    public void execute(TransportSnapshot snapshot) throws Exception {
        Project proj = systemData.getProjectByKey(snapshot.getApiKey()); // check if project exists
        if (proj == null) return;
        if (!proj.validateClient(snapshot.getClientKey())) return; // exit if client key is not registered

        Snapshot data = null;
        if (snapshot.getAuth() != null) { // check if user authorized
            // exit if validation of token are failed
            if (!validation.validateToken(snapshot.getAuth())) return;
        } else
            return;

        DBCollection collection = proj.getDatabase().getCollection(DATA_COLLECTION);

        switch (snapshot.getMethod()) {
            case "GET":
                data = NodeUtils.select(collection, snapshot);
                notifySubscriber(snapshot.getPath(), data);
                break;
            case "POST":
                NodeUtils.insert(collection, snapshot);
                break;
            case "PUT":
                NodeUtils.insert(collection, snapshot);
                data = new Snapshot();
                data.setData(snapshot.getData());

                notifySubscriber(snapshot.getPath(), data);
                break;
            case "DELETE":
                break;
            case "UPDATE":
                break;
        }
    }

    private void notifySubscriber(String path, Snapshot data) {
        this.msgTemplate.convertAndSend("/topic" + path, data);
    }
}

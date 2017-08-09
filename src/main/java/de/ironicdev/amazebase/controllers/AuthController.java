package de.ironicdev.amazebase.controllers;

import de.ironicdev.amazebase.models.Project;
import de.ironicdev.amazebase.SystemData;
import de.ironicdev.amazebase.auth.AuthModule;
import de.ironicdev.amazebase.auth.TokenValidation;
import de.ironicdev.amazebase.models.Credentials;
import de.ironicdev.amazebase.models.StatusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @Autowired
    private TokenValidation validation;

    @Autowired
    private AuthModule authModule;

    private SystemData systemData = SystemData.getInstance();

    @RequestMapping(value = "/{apiKey}/{clientKey}/auth", method = RequestMethod.POST)
    public ResponseEntity<StatusMessage> auth(@PathVariable("apiKey") String apiKey,
                                              @PathVariable("clientKey") String clientKey,
                                              @RequestBody Credentials credentials) {
        Project project = systemData.getProjectByKey(apiKey);
        StatusMessage msg = new StatusMessage();
        HttpStatus status = HttpStatus.OK;

        if (!project.validateClient(clientKey)) {
            msg.setErrorCode(4010);
            msg.setSuccess(false);
            msg.setMessage("unknown client key");
            status = HttpStatus.FORBIDDEN;
        } else {
            try {
                String token = authModule.authenticateUser(credentials, project);
                msg.setMessage(token);
                msg.setSuccess(true);
                msg.setErrorCode(0);
            } catch (Exception iaex) {
                msg.setMessage(iaex.getMessage());
                msg.setSuccess(false);
                msg.setErrorCode(4003);
                status = HttpStatus.FORBIDDEN;
            }
        }

        return new ResponseEntity<>(msg, status);
    }
}

package de.ironicdev.amazebase.controllers;

import de.ironicdev.amazebase.models.Client;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco on 27.07.17.
 */
@RestController
public class ClientManagementController {

    @RequestMapping(value = "/clients", method = RequestMethod.GET)
    public List<Client> getClients() {
        return new ArrayList<>();
    }

    @RequestMapping(value = "/clients/{projectId}", method = RequestMethod.GET)
    public Client getSingleClient(@PathVariable("clientId") String clientId) {
        return new Client();
    }

    @RequestMapping(value = "/clients/{clientId}", method = RequestMethod.PUT)
    public void updateClient(@RequestBody Client client, @PathVariable("clientId") String clientId) {

    }

    @RequestMapping(value = "/clients", method = RequestMethod.POST)
    public Client createClient(@RequestBody Client client) {
        return new Client();
    }

    @RequestMapping(value = "/clients/{clientId}", method = RequestMethod.DELETE)
    public void deleteClient(@PathVariable("clientId") String clientId) {

    }
}

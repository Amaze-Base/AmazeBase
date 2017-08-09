package de.ironicdev.amazebase.controllers;

import de.ironicdev.amazebase.models.DatabaseConfig;
import de.ironicdev.amazebase.models.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class SystemManagementController {

    @RequestMapping(value = "/sys/database", method = RequestMethod.PUT)
    public void updateDatabase(@RequestBody DatabaseConfig config) {

    }

    @RequestMapping(value = "/sys/admin/user", method = RequestMethod.POST)
    public void createAdminUser(@RequestBody User adminUser) {

    }

    @RequestMapping(value = "/sys/admin/user/{userId}", method = RequestMethod.PUT)
    public void updateAdminUser(@PathVariable("userId") String userId) {

    }

    @RequestMapping(value = "/sys/admin/user/{userId}", method = RequestMethod.DELETE)
    public void deleteAdminUser(@PathVariable("userId") String userId) {

    }

    @RequestMapping(value = "/sys/admin/user/{userId}", method = RequestMethod.GET)
    public void getAdminUser(@PathVariable("userId") String userId) {

    }
}

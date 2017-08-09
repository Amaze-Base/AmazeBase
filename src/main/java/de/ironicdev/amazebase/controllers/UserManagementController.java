package de.ironicdev.amazebase.controllers;

import de.ironicdev.amazebase.models.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class UserManagementController {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return new ArrayList<>();
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    public User getSingleUser(@PathVariable("userId") String userId) {
        return new User();
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
    public User updateUser(@RequestBody User user, @PathVariable("userId") String userId) {
        return new User();
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createUser(@RequestBody User user) {
        return new User();
    }

    @RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable("userId") String userId) {

    }
}

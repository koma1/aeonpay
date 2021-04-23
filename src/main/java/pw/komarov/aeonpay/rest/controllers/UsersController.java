package pw.komarov.aeonpay.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pw.komarov.aeonpay.data.users.User;
import pw.komarov.aeonpay.data.users.UserService;
import pw.komarov.aeonpay.rest.exceptions.NotFoundException;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping
    public Iterable<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/current")
    public User getCurrentUser() {
        return userService.currentUser();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id).orElseThrow(() -> new NotFoundException("User not found (id: %d)", id));
    }
}

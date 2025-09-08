package dev.louisa.victor.mock.rest.controller;

import dev.louisa.victor.mock.rest.dto.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class UserController {

    @GetMapping("/api/v1/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<User> getAllUser(@PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
        
    }
    
    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createUser(@RequestBody User user) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }
}
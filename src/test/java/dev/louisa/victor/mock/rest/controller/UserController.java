package dev.louisa.victor.mock.rest.controller;

import dev.louisa.victor.mock.rest.dto.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class UserController {

    @GetMapping("/api/v1/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }

    @GetMapping("/api/v1/users/{id}/raw")
    public User getUserRaw(@PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }

    @GetMapping("/api/v1/users/{id}/details")
    public ResponseEntity<User> getUser(@PathVariable("id") String id, @RequestHeader("Authorization") String authorizationHeader) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<List<User>> getAllUsers() {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
        
    }
    
    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void postUser(@RequestBody User user) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }

    @PutMapping("/api/v1/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putUser(@PathVariable("id") String id, @RequestBody User user) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }

    @PatchMapping("/api/v1/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchUser(@PathVariable("id") String id, @RequestBody User user) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }

    @DeleteMapping("/api/v1/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not yet implemented: UserControllerStub.getUser");
    }
}
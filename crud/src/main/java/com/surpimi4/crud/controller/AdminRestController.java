package com.surpimi4.crud.controller;

import com.surpimi4.crud.dto.UserDTO;
import com.surpimi4.crud.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserServiceImpl userService;

    public AdminRestController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUserList() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/user")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserDTO user,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            System.out.println("Validation Errors: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<UserDTO> deleteCurrentUser(@PathVariable Integer id) {
        UserDTO deletedUser = userService.findUserById(id);
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.findUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id,
                                        @RequestBody @Valid UserDTO user,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неккоректные данные пользователя");
        }
        user.setId(id);
        userService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}


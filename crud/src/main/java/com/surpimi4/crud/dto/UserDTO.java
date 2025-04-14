package com.surpimi4.crud.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.surpimi4.crud.model.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;


@AllArgsConstructor

@Getter
@Setter
public class UserDTO {

    private Integer id;

    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно быть между 2 и 30 символами")
    private String name;


    @Min(value = 0, message = "Возраст должен быть больше 0")
    private Integer age;


    @NotEmpty(message = "Поле почты не должно быть пустым")
    @Email(message = "Почта должна быть валидной")
    private String email;


    @NotEmpty
    private String password;
    @JsonIgnore
    private Set<Role> roles;

    private Set<String> role;


    public UserDTO(Integer id,
                   String name,
                   Integer age,
                   String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public UserDTO(Integer id,
                   String name,
                   Integer age,
                   String email,
                   String password,
                   Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public UserDTO() {
    }


    @Override
    public String toString() {
        return "User" +
                " name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'';
    }
}


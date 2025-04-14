package com.surpimi4.crud.repository;

import com.surpimi4.crud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Optional<User> findByName(String username);

}

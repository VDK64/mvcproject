package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByActivationCode(String code);
}

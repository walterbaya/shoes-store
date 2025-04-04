package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.User;
import com.shoesstore.shoesstore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}


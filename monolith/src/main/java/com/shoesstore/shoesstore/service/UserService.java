package com.shoesstore.shoesstore.service;

import com.shoesstore.shoesstore.model.User;
import com.shoesstore.shoesstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}


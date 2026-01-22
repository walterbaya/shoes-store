package com.shoesstore.shoesstore.initializer;

import com.shoesstore.shoesstore.model.User;
import com.shoesstore.shoesstore.model.enums.Role;
import com.shoesstore.shoesstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if(userRepository.count() == 0){
            userRepository.save(new User("admin", passwordEncoder.encode("1234"), "Administrador Principal",  "admin@example.com", Role.ADMIN));
            userRepository.save(new User("seller", passwordEncoder.encode("1234"), "Vendedor Uno","seller1@example.com" , Role.SELLER));
            userRepository.save(new User("stockmanager", passwordEncoder.encode("1234"), "Gestor de Stock", "stockmanager@example.com",Role.STOCK_MANAGER));
        }
    }
}

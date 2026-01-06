package com.shoesstore.shoesstore.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    void shouldMatchPassword1234WithStoredBcryptHash() {
        String rawPassword = "1234";
        String encodedPassword =
                "{bcrypt}$2a$12$JD7Z0T.NAddwgx3H2Ohg8eWq6pLc64jvRFEKRlaix4K9Tbg6b62Dm";

        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}

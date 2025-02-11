package dev.nincodedo.ninbot.components.users;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

package org.itstep.transcriblyai.modules.auth.services;

import lombok.RequiredArgsConstructor;
import org.itstep.transcriblyai.modules.auth.models.UserModel;
import org.itstep.transcriblyai.modules.auth.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserModel registerUser(String username, String password) {
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public UserModel findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}

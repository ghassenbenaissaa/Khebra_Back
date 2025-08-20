package com.example.Khebra.service.impl;

import com.example.Khebra.entity.User;
import com.example.Khebra.repository.ExpertRepository;
import com.example.Khebra.repository.UserRepository;
import com.example.Khebra.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {


    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;


    public User getUserEntityByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(client -> (User) client)
                .orElseGet(() -> expertRepository.findByEmail(email)
                        .map(expert -> (User) expert)
                        .orElseThrow(() -> new RuntimeException("User not found with email: " + email)));
    }

    public void BanUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(true);
        userRepository.save(user);
    }
    public void UnBanUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(false);
        userRepository.save(user);
    }

}


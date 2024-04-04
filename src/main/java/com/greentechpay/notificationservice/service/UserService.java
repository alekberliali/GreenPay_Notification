package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.entity.AppUser;
import com.greentechpay.notificationservice.exception.UserNotFoundException;
import com.greentechpay.notificationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    protected AppUser getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User could not found by id: " + id));
    }

    protected boolean existsById(String id) {
        return userRepository.existsById(id);
    }
}

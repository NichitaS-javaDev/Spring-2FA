package org.example.service.impl;

import jakarta.validation.Valid;
import org.example.entity.User;
import org.example.repo.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private IUserRepo userRepo;

    public User saveUser(@Valid User user){
        return userRepo.save(user);
    }
}

package org.example.service;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.User;

import java.util.Map;

public interface IAuthenticationService {
    Map<String, String> authenticate(String username, @Nullable String otp, HttpServletRequest req, HttpServletResponse res);
    Map<String, String> register(User newUser);
}

package org.example.service.impl;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.entity.User;
import org.example.mfa.MFATokenManager;
import org.example.security.MfaBasedAuthenticationToken;
import org.example.service.IAuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final MFATokenManager mfaTokenManager;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    public Map<String, String> authenticate(String username, @Nullable String otp, HttpServletRequest req, HttpServletResponse res) throws UsernameNotFoundException {
        Map<String, String> response = new HashMap<>();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (Objects.nonNull(userDetails)){
            try {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication authentication = authenticationManager.authenticate(
                        new MfaBasedAuthenticationToken(username, userDetails.getPassword(), otp));
                context.setAuthentication(authentication);
                this.securityContextHolderStrategy.setContext(context);
                this.securityContextRepository.saveContext(context, req, res);
            } catch (AuthenticationException e) {
                response.put("error", e.getMessage());
                return response;
            }

            String role = userDetails.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            response.put("role", role);
            response.put("username", username);

            return response;
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public Map<String, String> register(User newUser) {
        Map<String, String> response = new HashMap<>();

        if (newUser.isMfaEnabled()) {
            String secret = mfaTokenManager.generateSecretKey();
            newUser.setSecret(secret);
        }

        userService.saveUser(newUser);

        response.put("role", newUser.getRole());
        response.put("email", newUser.getEmail());
        response.put("username", newUser.getUsername());

        return response;
    }

}

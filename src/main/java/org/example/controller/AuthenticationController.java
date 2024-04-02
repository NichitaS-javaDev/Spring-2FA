package org.example.controller;

import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.validation.Valid;
import org.example.entity.AwareUser;
import org.example.entity.User;
import org.example.mfa.MFATokenManager;
import org.example.reqEntity.LoginRequest;
import org.example.reqEntity.RegistrationRequest;
import org.example.security.MfaBasedAuthenticationToken;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private MFATokenManager mfaTokenManager;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger log = Logger.getLogger(AuthController.class.getName());

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest credentials) {
        String username = credentials.getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (Objects.nonNull(userDetails)){
            Authentication authentication = authenticationManager.authenticate(
                    new MfaBasedAuthenticationToken(username, credentials.getPassword(), credentials.getOtp()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String role = userDetails.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);

            Map<String, String> response = new HashMap<>();
            response.put("role", role);
            response.put("username", username);

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegistrationRequest registrationRequest){
        User newUser = User.builder()
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .email(registrationRequest.getEmail())
                .role(registrationRequest.getRole())
                .mfaEnabled(registrationRequest.isMfaEnabled())
                .build();

        if (registrationRequest.isMfaEnabled()){
            String secret = mfaTokenManager.generateSecretKey();
            newUser.setSecret(secret);
        }

        Map<String, String> response = new HashMap<>();
        try {
            userService.saveUser(newUser);

            response.put("role", newUser.getRole());
            response.put("email", newUser.getEmail());
            response.put("username", newUser.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e){
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @GetMapping("/getQrCode")
    public ResponseEntity<Map<String, String>> getQrCode(@RequestParam("user") String usernameOrEmail) {
        Map<String, String> response = new HashMap<>();
        User user = ((AwareUser) userDetailsService.loadUserByUsername(usernameOrEmail)).getUser();

        if (user.isMfaEnabled()){
            try {
                String base64QRCode = mfaTokenManager.getQRCode(user.getSecret());
                response.put("secret", user.getSecret());
                response.put("image", base64QRCode);

                return ResponseEntity.ok(response);
            } catch (QrGenerationException e) {
                response.put("error", e.getMessage());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } else {
            response.put("error", "2FA not enabled");

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
}

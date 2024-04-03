package org.example.controller;

import dev.samstevens.totp.exceptions.QrGenerationException;
import lombok.AllArgsConstructor;
import org.example.entity.User;
import org.example.mfa.MFATokenManager;
import org.example.security.AwareUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private MFATokenManager mfaTokenManager;
    private UserDetailsService userDetailsService;

    @GetMapping("/getQrCode")
    public ResponseEntity<Map<String, String>> getQrCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = ((AwareUser) userDetailsService.loadUserByUsername(authentication.getName())).getUser();
        Map<String, String> response = new HashMap<>();

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

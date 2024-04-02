package org.example.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class AuthToken extends UsernamePasswordAuthenticationToken {
    private final String otp;
    public AuthToken(Object principal, Object credentials, String otp) {
        super(principal, credentials);
        this.otp = otp;
    }

//    public String getOtp() {
//        return otp;
//    }
}

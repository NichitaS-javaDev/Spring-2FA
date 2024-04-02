package org.example.security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class MfaBasedAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String otp;
    public MfaBasedAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String otp) {
        super(principal, credentials, authorities);
        this.otp = otp;
    }

    public MfaBasedAuthenticationToken(Object principal, Object credentials, String otp) {
        super(principal, credentials);
        this.otp = otp;
    }
}

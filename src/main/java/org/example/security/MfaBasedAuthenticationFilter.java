package org.example.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class MfaBasedAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    public MfaBasedAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String username = this.obtainUsername(request);
            username = normalize(username);
            String password = this.obtainPassword(request);
            password = password != null ? password : "";
            String otp = request.getParameter("otp");
            otp = normalize(otp);
            MfaBasedAuthenticationToken authRequest =
                    new MfaBasedAuthenticationToken(username, password, otp);
            this.setDetails(request, authRequest);

            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    private String normalize(String value){
        return value != null ? value.trim() : "";
    }
}

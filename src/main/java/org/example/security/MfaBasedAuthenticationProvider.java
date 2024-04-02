package org.example.security;

import org.example.entity.AwareUser;
import org.example.entity.User;
import org.example.mfa.MFATokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
//@Setter
public class AuthProvider extends DaoAuthenticationProvider {
    //    private final UserDetailsService userDetailsService;
    private final MFATokenManager mfaTokenManager;

    @Autowired
    public AuthProvider(UserDetailsService userDetailsService, MFATokenManager mfaTokenManager) {
        setUserDetailsService(userDetailsService);
        this.mfaTokenManager = mfaTokenManager;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);

        User user = ((AwareUser) userDetails).getUser();
        if (user.isMfaEnabled()) {
            AuthToken auth = (AuthToken) authentication;
            String otp = auth.getOtp();
            if (!mfaTokenManager.verifyTotp(otp, user.getSecret())) {
                throw new BadCredentialsException("Bad credentials");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthToken.class);
    }
}

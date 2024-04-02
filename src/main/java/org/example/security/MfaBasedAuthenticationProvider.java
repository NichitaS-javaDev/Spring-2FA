package org.example.security;

import org.example.entity.User;
import org.example.mfa.MFATokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

@Component
public class MfaBasedAuthenticationProvider extends DaoAuthenticationProvider {
    private final MFATokenManager mfaTokenManager;

    @Autowired
    public MfaBasedAuthenticationProvider(UserDetailsService userDetailsService, MFATokenManager mfaTokenManager) {
        setUserDetailsService(userDetailsService);
        this.mfaTokenManager = mfaTokenManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication, () ->
                this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports", "Only UsernamePasswordAuthenticationToken is supported"));

        String username = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
        AwareUser awareUser = (AwareUser) super.getUserDetailsService().loadUserByUsername(username);
        if (Objects.nonNull(awareUser)) {

            super.getPreAuthenticationChecks().check(awareUser);
            this.additionalAuthenticationChecks(awareUser, (MfaBasedAuthenticationToken) authentication);
            super.getPostAuthenticationChecks().check(awareUser);

            MfaBasedAuthenticationToken mfaToken = new MfaBasedAuthenticationToken(
                    awareUser, authentication.getCredentials(), awareUser.getAuthorities(), ((MfaBasedAuthenticationToken) authentication).getOtp()
            );
            mfaToken.setDetails(authentication.getDetails());

            return mfaToken;
        } else {
            super.logger.debug("Failed to find user '" + username + "'");
            throw new BadCredentialsException("Invalid username");
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (AuthenticationException e) {
            super.logger.debug(e.getMessage());
        }

        User user = ((AwareUser) userDetails).getUser();
        if (user.isMfaEnabled()) {
            MfaBasedAuthenticationToken auth = (MfaBasedAuthenticationToken) authentication;
            String otp = auth.getOtp();
            if (!mfaTokenManager.verifyTotp(otp, user.getSecret())) {
                throw new BadCredentialsException("Bad credentials");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(MfaBasedAuthenticationToken.class);
    }
}

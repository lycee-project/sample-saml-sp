package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;

public class LoginUserAuthentication extends AbstractAuthenticationToken {

    private final Authentication authentication;

    private final LoginUserDetails userDetails;

    public LoginUserAuthentication(
            Authentication authentication,
            LoginUserDetails userDetails) {
        super(userDetails.getAuthorities());
        this.authentication = authentication;
        setAuthenticated(true);
        setDetails(userDetails);
        this.userDetails = userDetails;

    }

    @Override
    public Object getCredentials() {
        return userDetails.getPassword();
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}

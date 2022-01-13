package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public abstract class LoginUserAuthenticationToken extends AbstractAuthenticationToken {
    private final LoginUserDetails loginUserDetails;

    public LoginUserAuthenticationToken(
            Collection<? extends GrantedAuthority> authorities,
            LoginUserDetails userDetails) {
        super(authorities);
        loginUserDetails = userDetails;
    }

    public LoginUserDetails getLoginUserDetails() {
        return loginUserDetails;
    }

}

package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

public class LoginUserAuthentication extends Saml2Authentication {
    private final LoginUserDetails loginUserDetails;

    public LoginUserAuthentication(
            Saml2Authentication authentication,
            LoginUserDetails userDetails) {
        super((AuthenticatedPrincipal) authentication.getPrincipal(),
                authentication.getSaml2Response(),
                userDetails.getAuthorities());
        loginUserDetails = userDetails;
    }

    public LoginUserDetails getLoginUserDetails() {
        return loginUserDetails;
    }
}

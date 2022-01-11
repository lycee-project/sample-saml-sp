package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;

public class LoginUserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    LoginUserDetailsService loginUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Saml2AuthenticationToken samlAuth = (Saml2AuthenticationToken) authentication;

        RelyingPartyRegistration.AssertingPartyDetails details = samlAuth.getRelyingPartyRegistration().getAssertingPartyDetails();



        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Saml2AuthenticationToken.class.isAssignableFrom(authentication);
    }
}

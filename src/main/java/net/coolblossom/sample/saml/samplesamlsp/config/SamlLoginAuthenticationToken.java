package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.util.Assert;

/**
 * SAML認証の際に生成する認証オブジェクト
 */
public class SamlLoginAuthenticationToken extends LoginUserAuthenticationToken {

    private final AuthenticatedPrincipal principal;

    private final String saml2Response;

    public SamlLoginAuthenticationToken(
            Saml2Authentication authentication,
            LoginUserDetails userDetails) {
        this((AuthenticatedPrincipal) authentication.getPrincipal(),
                authentication.getSaml2Response(), userDetails);
    }

    /**
     * Construct a {@link Saml2Authentication} using the provided parameters
     * @param principal the logged in user
     * @param saml2Response the SAML 2.0 response used to authenticate the user
     */
    public SamlLoginAuthenticationToken(
            AuthenticatedPrincipal principal, String saml2Response,
            LoginUserDetails userDetails
    ) {
        super(userDetails.getAuthorities(), userDetails);
        Assert.notNull(principal, "principal cannot be null");
        Assert.hasText(saml2Response, "saml2Response cannot be null");
        this.principal = principal;
        this.saml2Response = saml2Response;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * Returns the SAML response object, as decoded XML. May contain encrypted elements
     * @return string representation of the SAML Response XML object
     */
    public String getSaml2Response() {
        return this.saml2Response;
    }

    @Override
    public Object getCredentials() {
        return getSaml2Response();
    }

}

package net.coolblossom.sample.saml.samplesamlsp.config;

public class DummyLoginAuthenticationToken extends LoginUserAuthenticationToken {


    public DummyLoginAuthenticationToken(LoginUserDetails userDetails) {
        super(userDetails.getAuthorities(), userDetails);
    }


    @Override
    public Object getCredentials() {
        return getLoginUserDetails().getPassword();
    }

    @Override
    public Object getPrincipal() {
        return getLoginUserDetails();
    }
}

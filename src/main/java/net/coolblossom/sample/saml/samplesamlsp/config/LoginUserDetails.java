package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class LoginUserDetails extends User {

    public LoginUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public boolean isRear() {
        return getUsername().endsWith("_rear");
    }

}

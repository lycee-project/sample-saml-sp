package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * ログインユーザーの情報を格納するオブジェクト
 */
public class LoginUserDetails extends User {

    /**
     *
     * @param username
     * @param password
     * @param authorities
     */
    public LoginUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public boolean isRear() {
        return getUsername().endsWith("_rear");
    }

}

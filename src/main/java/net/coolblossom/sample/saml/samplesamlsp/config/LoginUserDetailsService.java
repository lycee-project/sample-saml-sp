package net.coolblossom.sample.saml.samplesamlsp.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoginUserDetailsService implements UserDetailsService {
    @Value("${app.admin.login.users}")
    private String adminLoginUsers;

    @Override
    public LoginUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ユーザー名から権限取得
        Collection<GrantedAuthority> authorities = getGrantedAuthorities(username);

        // 認証情報のユーザー情報を生成
        LoginUserDetails userDetails = new LoginUserDetails(username, "password", authorities);

        log.info("load User: {}({})", userDetails.getUsername(),
                userDetails.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority
                ).collect(Collectors.joining(","))
                );

        return userDetails;
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(String username) {
        boolean isAdmin = Arrays.asList(adminLoginUsers.split(",")).contains(username);
        if (isAdmin) {
            return AuthorityUtils.createAuthorityList("AUTH_USER", "AUTH_ADMIN");
        } else {
            return AuthorityUtils.createAuthorityList("AUTH_USER");
        }
    }
}

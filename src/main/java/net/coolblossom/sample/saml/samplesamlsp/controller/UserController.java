package net.coolblossom.sample.saml.samplesamlsp.controller;

import lombok.extern.slf4j.Slf4j;
import net.coolblossom.sample.saml.samplesamlsp.config.LoginUserAuthentication;
import net.coolblossom.sample.saml.samplesamlsp.config.LoginUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@Slf4j
public class UserController {

    @RequestMapping("/user/")
    public String user(
            LoginUserAuthentication authentication) {
        LoginUserDetails details = authentication.getLoginUserDetails();
        log.info("★★ access user page: username={}, isRear={} ({})",
                details.getUsername(),
                details.isRear(),
                details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))
        );

        return "user/index";
    }

}

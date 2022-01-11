package net.coolblossom.sample.saml.samplesamlsp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@Slf4j
public class UserController {

    @RequestMapping("/user/")
    public String user(Authentication authentication) {
        log.info("★★ access user page: username={} ({})",
                authentication.getName(),
                authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","))
        );

        return "user/index";
    }

}

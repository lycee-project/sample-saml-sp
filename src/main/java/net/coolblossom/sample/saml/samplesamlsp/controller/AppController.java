package net.coolblossom.sample.saml.samplesamlsp.controller;

import lombok.extern.slf4j.Slf4j;
import net.coolblossom.sample.saml.samplesamlsp.config.LoginUserAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class AppController {

    @GetMapping("/")
    public String index(
            Model model,
            LoginUserAuthenticationToken authentication) {
        log.info("authentication: {}", authentication);
        if (authentication != null) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("name", authentication.getName());
            model.addAttribute("authorities", authentication.getAuthorities());
        } else {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("name", null);
            model.addAttribute("authorities", null);
        }
        return "index";
    }
}

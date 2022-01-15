package net.coolblossom.sample.saml.samplesamlsp.controller;

import net.coolblossom.sample.saml.samplesamlsp.config.LoginUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dummy")
public class DummyLoginController {

    @GetMapping("/login")
    public String index(@AuthenticationPrincipal LoginUserDetails loginUserDetails) {
        if (loginUserDetails!=null) {
            return "redirect:/user/";
        }
        return "dummy/login";
    }

    @RequestMapping("/dispatch")
    public String dispatch() {
        return "redirect:/user/";
    }
}

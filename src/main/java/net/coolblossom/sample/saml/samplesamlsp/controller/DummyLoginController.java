package net.coolblossom.sample.saml.samplesamlsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dummy")
public class DummyLoginController {

    @GetMapping("/login")
    public String index() {
        return "dummy/login";
    }

    @GetMapping("/dispatch")
    public String dispatch() {
        return "redirect:/user/";
    }
}

package net.coolblossom.sample.saml.samplesamlsp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecureController {

    @RequestMapping("/secure/")
    public String index() {
        return "secure/index";
    }
}

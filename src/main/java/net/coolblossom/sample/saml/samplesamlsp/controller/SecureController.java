package net.coolblossom.sample.saml.samplesamlsp.controller;

import net.coolblossom.sample.saml.samplesamlsp.config.LoginUserDetails;
import net.coolblossom.sample.saml.samplesamlsp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecureController {

    @Autowired
    private LogService logService;


    @RequestMapping("/secure/")
    public String index() {
        logService.log("セキュアなページです");
        return "secure/index";
    }
}

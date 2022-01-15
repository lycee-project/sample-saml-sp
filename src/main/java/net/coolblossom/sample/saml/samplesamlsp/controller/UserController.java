package net.coolblossom.sample.saml.samplesamlsp.controller;

import lombok.extern.slf4j.Slf4j;
import net.coolblossom.sample.saml.samplesamlsp.config.LoginUserDetails;
import net.coolblossom.sample.saml.samplesamlsp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private LogService logService;

    @RequestMapping("/user/")
    public String user(
            @AuthenticationPrincipal LoginUserDetails userDetails
    ) {
        logService.log(userDetails, "ログインすれば見れるページ");
        return "user/index";
    }

}

package net.coolblossom.sample.saml.samplesamlsp.service;

import lombok.extern.slf4j.Slf4j;
import net.coolblossom.sample.saml.samplesamlsp.config.LoginUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogService {

    public void log(String message) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        StackTraceElement st = Thread.currentThread().getStackTrace()[1];
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserDetails) {
            LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
            log_internal(st, userDetails, message);
        }else{
            log_internal(st, null, message);
        }
    }

    public void log(LoginUserDetails loginUserDetails, String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[1];

        log_internal(ste, loginUserDetails, message);
    }

    private void log_internal(StackTraceElement ste, LoginUserDetails loginUserDetails,
                         String message) {
        String username = (loginUserDetails!=null)? loginUserDetails.getUsername() : "[ANONYMOUS]";
        Boolean isRear = (loginUserDetails!=null)?loginUserDetails.isRear():null;
        log.info(
                "method:{}#{}\tusername:{}\trear:{}\t{}",
                ste.getClassName(),
                ste.getMethodName(),
                username,
                isRear,
                message
                );
    }

}

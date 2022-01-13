package net.coolblossom.sample.saml.samplesamlsp.config;

import org.opensaml.saml.saml2.core.Assertion;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;

import java.util.Objects;

public class LoginResponseAuthenticationConverter
    implements Converter<
        OpenSaml4AuthenticationProvider.ResponseToken, LoginUserAuthenticationToken>
{

    private final LoginUserDetailsService loginUserDetailsService;

    public LoginResponseAuthenticationConverter(
            LoginUserDetailsService loginUserDetailsService
    ) {
        this.loginUserDetailsService = loginUserDetailsService;
    }

    @Override
    public LoginUserAuthenticationToken convert(OpenSaml4AuthenticationProvider.ResponseToken responseToken) {
        // SAML2レスポンスから認証情報を生成
        Saml2Authentication authentication = OpenSaml4AuthenticationProvider
                .createDefaultResponseAuthenticationConverter()
                .convert(responseToken);
        Objects.requireNonNull(authentication);

        // UserIDからシステムで利用するUserDetailsを取得
        Assertion assertion = responseToken.getResponse().getAssertions().get(0);
        String nameId = assertion.getSubject().getNameID().getValue();
        LoginUserDetails userDetails = loginUserDetailsService.loadUserByUsername(nameId);

        // 認証情報の生成
        return new SamlLoginAuthenticationToken(authentication, userDetails);

    }
}

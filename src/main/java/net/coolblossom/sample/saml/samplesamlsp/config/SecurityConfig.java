package net.coolblossom.sample.saml.samplesamlsp.config;

import lombok.extern.slf4j.Slf4j;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

    @Autowired
    LoginUserDetailsService loginUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // トップページは認証なし
        http.authorizeRequests()
                .antMatchers("/", "/logout")
                .permitAll();

        RelyingPartyRegistrationResolver relyingPartyRegistrationResolver
                = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);

        Saml2MetadataFilter filter = new Saml2MetadataFilter(
                relyingPartyRegistrationResolver,
                new OpenSamlMetadataResolver()
        );

        // 権限の設定
        /*
        http.authorizeRequests()
                .antMatchers("/manage/**").hasRole("MANAGER")
                .antMatchers("/user/**").hasRole("USER")
                ;
        *
         */

        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(responseToken -> {
            // SAML2レスポンスから認証情報を生成

            /*
             以下のAuthentication生成処理は，OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter()
             からコピーしたもの（GrantedAuthorityを動的にしたいため）
             */
            Response response = responseToken.getResponse();
            Saml2AuthenticationToken token = responseToken.getToken();
            Assertion assertion = CollectionUtils.firstElement(response.getAssertions());
            assert assertion != null;

            String username = assertion.getSubject().getNameID().getValue();
            Map<String, List<Object>> attributes = getAssertionAttributes(assertion);

            DefaultSaml2AuthenticatedPrincipal principal = new DefaultSaml2AuthenticatedPrincipal(username, attributes);

            String registrationId = responseToken.getToken().getRelyingPartyRegistration().getRegistrationId();
            principal.setRelyingPartyRegistrationId(registrationId);

            // UserIDからシステムで利用するUserDetailsを取得
            LoginUserDetails userDetails = loginUserDetailsService.loadUserByUsername(username);

            // 認証情報の生成
            Saml2Authentication authentication = new Saml2Authentication(principal, token.getSaml2Response(), userDetails.getAuthorities());
            authentication.setDetails(userDetails);

            return authentication;
        });

        // user配下はSAML認証
        http
                // CSRF対応・無効
                .csrf().disable()

                // SAML認証の設定
                .saml2Login(saml2 ->
                        saml2.authenticationManager(new ProviderManager(authenticationProvider))
                                .defaultSuccessUrl("/user/")
                )
                .addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class)
                .antMatcher("/**")

                .authorizeHttpRequests()
                .antMatchers("/", "/logout").permitAll()
                .antMatchers("/user/").hasAuthority("AUTH_USER")
                .anyRequest()
                .authenticated();
    }

    private static Map<String, List<Object>> getAssertionAttributes(Assertion assertion) {
        Map<String, List<Object>> attributeMap = new LinkedHashMap<>();
        for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
            for (Attribute attribute : attributeStatement.getAttributes()) {
                List<Object> attributeValues = new ArrayList<>();
                for (XMLObject xmlObject : attribute.getAttributeValues()) {
                    Object attributeValue = getXmlObjectValue(xmlObject);
                    if (attributeValue != null) {
                        attributeValues.add(attributeValue);
                    }
                }
                attributeMap.put(attribute.getName(), attributeValues);
            }
        }
        return attributeMap;
    }

    private static Object getXmlObjectValue(XMLObject xmlObject) {
        if (xmlObject instanceof XSAny) {
            return ((XSAny) xmlObject).getTextContent();
        }
        if (xmlObject instanceof XSString) {
            return ((XSString) xmlObject).getValue();
        }
        if (xmlObject instanceof XSInteger) {
            return ((XSInteger) xmlObject).getValue();
        }
        if (xmlObject instanceof XSURI) {
            return ((XSURI) xmlObject).getURI();
        }
        if (xmlObject instanceof XSBoolean) {
            XSBooleanValue xsBooleanValue = ((XSBoolean) xmlObject).getValue();
            return (xsBooleanValue != null) ? xsBooleanValue.getValue() : null;
        }
        if (xmlObject instanceof XSDateTime) {
            return ((XSDateTime) xmlObject).getValue();
        }
        return null;
    }
}

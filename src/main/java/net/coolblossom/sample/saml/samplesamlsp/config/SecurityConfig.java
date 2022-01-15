package net.coolblossom.sample.saml.samplesamlsp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.servlet.filter.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Configuration
    @Order(1)
    public static class DummyLoginSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        LoginUserDetailsService loginUserDetailsService;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(
                    new AuthenticationProvider() {

                        @Override
                        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                            String username = authentication.getName();


                            LoginUserDetails userDetails = loginUserDetailsService.loadUserByUsername(username);
                            return new DummyLoginAuthenticationToken(userDetails);
                        }

                        @Override
                        public boolean supports(Class<?> authentication) {
                            return DummyLoginAuthenticationToken.class.isAssignableFrom(authentication);
                        }
                    }
            );
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/dummy/**")
                    .userDetailsService(loginUserDetailsService)
                    .formLogin()
                    .loginPage("/dummy/login")
                    .successForwardUrl("/dummy/dispatch")
            ;
        }
    }

    @Configuration
    @Order(2)
    public static class SamlSecurityConfig extends WebSecurityConfigurerAdapter {
        @Autowired
        RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

        @Autowired
        LoginUserDetailsService loginUserDetailsService;

        @Bean
        public LoginResponseAuthenticationConverter loginResponseAuthenticationConverter() {
            return new LoginResponseAuthenticationConverter(loginUserDetailsService);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
             * SAML認証で使う情報の設定
             */
            RelyingPartyRegistrationResolver relyingPartyRegistrationResolver
                    = new DefaultRelyingPartyRegistrationResolver(relyingPartyRegistrationRepository);

            Saml2MetadataFilter filter = new Saml2MetadataFilter(
                    relyingPartyRegistrationResolver,
                    new OpenSamlMetadataResolver()
            );

            // SAMLレスポンスを処理するProviderの設定
            // 本処理はloginResponseAuthenticationConverterが担う
            OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
            authenticationProvider.setResponseAuthenticationConverter(loginResponseAuthenticationConverter());

            // SAML認証の適用範囲の設定
            http
                // 権限設定
                .authorizeHttpRequests()
                    .antMatchers("/user/**").hasAuthority("AUTH_USER")
                    .antMatchers("/secure/**").hasAuthority("AUTH_ADMIN")
                    .anyRequest().authenticated()
                .and()

                // CSRF対応・無効
                .csrf().disable()

                // SAML認証の設定
                .saml2Login(saml2 ->
                        saml2
                            .authenticationManager(new ProviderManager(authenticationProvider))
                            .defaultSuccessUrl("/user/")
                )
                    .addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class)
                    .antMatcher("/**")

                // ログアウト処理
                .logout()
                    .logoutRequestMatcher(
                            new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                .and()


                // セッション管理
                .sessionManagement()
                    .invalidSessionUrl("/")
            ;
        }
    }
}

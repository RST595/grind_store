package com.bmxstore.grind_store.configuration.security;

import com.bmxstore.grind_store.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.bmxstore.grind_store.database.entity.user.UserRole.ADMIN;
import static com.bmxstore.grind_store.database.entity.user.UserRole.USER;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/categories/all").hasAnyAuthority(ADMIN.name(), USER.name())
                    .antMatchers("/", "index", "/admin/registration").permitAll()
                    .antMatchers("/**").hasAuthority(ADMIN.name())
                    .anyRequest()
                    .authenticated()
                .and() //custom login page
                    .formLogin()
                    .loginPage("/login").permitAll()
                    //place to redirect after successful authentication
                    .defaultSuccessUrl("/admin/panel", true)
                    .passwordParameter("password") //specify name of parameter for html
                    .usernameParameter("username") //specify name of parameter for html
                .and() // extend remember me options to 21 day
                    .rememberMe()
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                    .key("SecretKeyToGrindStore2022")
                    .rememberMeParameter("remember-me") //specify name of parameter for html
                .and() //logout and clear JSESSIONID and remember-me cookies
                    .logout()
                    .logoutUrl("/logout")
                    // remove next line when  csrf is enabled
                    // https://docs.spring.io/spring-security/site/docs/4.2.12.RELEASE/apidocs/org/springframework/security/config/annotation/web/configurers/LogoutConfigurer.html
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }

}

package com.example.security.config;

import com.example.security.config.service.RememberMeService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationProvider customAuthenticationProvider) throws Exception {
        http.
                authorizeHttpRequests((auth) -> {
                    auth
                            .requestMatchers("/loginUser", "/register", "/home", "/login").permitAll()
                            .requestMatchers("/account/**").hasAnyAuthority("CUSTOMER", "ADMIN")
                            .requestMatchers("/endpoint").hasAuthority("USER")
                           // .requestMatchers("/resource/{name}").access(new WebExpressionAuthorizationManager("#name==authentication.name"))
                           // .requestMatchers(HttpMethod.GET).hasAuthority("read")
                           // .requestMatchers(HttpMethod.POST).hasAuthority("write")
                            .anyRequest().denyAll();

                })
                .formLogin(form -> form.loginPage("/loginUser").defaultSuccessUrl("/account/hi"))
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {

        List<AuthenticationProvider> list = new ArrayList<>();

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider(userDetailsService);
        list.add(authProvider);
        list.add(customAuthenticationProvider);

        return new ProviderManager(list);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

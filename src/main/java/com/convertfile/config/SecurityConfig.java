package com.convertfile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/login", "/register", "/css/**", "/js/**", "/images/**", "/resources/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/home").authenticated()
                .anyRequest().permitAll()
            )
            
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/?error=oauth2_failure")
            )
            
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )

            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/login/oauth2/**")
            );
        
        return http.build();
    }
}

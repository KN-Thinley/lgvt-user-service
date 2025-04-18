package com.lgvt.user_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity // Go with this config instead of the default config of spring security
public class SecurityConfig {
    @Autowired
    private CustomDetailsService customDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(customizer -> customizer.disable());
        httpSecurity.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());
        // httpSecurity.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        // httpSecurity.formLogin(Customizer.withDefaults());
        // httpSecurity.httpBasic(Customizer.withDefaults());
        // httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }

    @Bean // Inorder to implement the database authentication
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder()); // PasswordEncoder
        provider.setUserDetailsService(customDetailsService);
        return provider;
    }

    // @Bean // UsernamePasswordAuthenticationFilter >>> To Verify User Credentials
    // public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    //     UserDetails user1 = User.withDefaultPasswordEncoder().username("Aue").password("aue12345").roles("USER")
    //             .build();
    //     UserDetails user2 = User.withDefaultPasswordEncoder().username("Cool").password("cool12345").roles("admin")
    //             .build();
    //     return new InMemoryUserDetailsManager(user1, user2);
    // }
}
package com.lgvt.user_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity // Go with this config instead of the default config of spring security
public class SecurityConfig {
    @Autowired
    private CustomDetailsService customDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(customizer -> customizer.disable());
        httpSecurity.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/api/auth/voter/register", "/api/auth/voter/login",
                        "/api/auth/verify-login-otp", "/api/auth/voter/verify-otp", "/api/auth/forgot-password",
                        "/api/auth/verify-forgot-password-otp", "/api/auth/resent-otp",
                        "/api/auth/voter/login", "/api/auth/user/login", "/api/auth/reset-password",
                        "/api/auth/super-admin/invitation/verify",
                        "/api/auth/super-user/register", "/api/auth/super-admin/invitation/register")
                .permitAll()
                .requestMatchers("/api/auth/voter/update-password").hasAuthority("VOTER")
                .requestMatchers(
                        "/api/auth/super-user/register", "/api/auth/admin/voters","/api/auth/admin/voter")
                .hasAuthority("ADMIN")
                .requestMatchers("/api/auth/super-admin/info",
                        "/api/auth/super-admin/statistics", "/api/auth/super-admin/invitation",
                        "/api/auth/super-admin/invitation/resent", "/api/auth/super-admin/admins",
                        "/api/auth/super-admin/admin")
                .hasAuthority("SUPER_ADMIN")
                .requestMatchers("/api/auth/user/reset-password")
                .hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                .anyRequest().authenticated());
        httpSecurity.httpBasic(Customizer.withDefaults());
        httpSecurity.sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS));
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean // Inorder to implement custom authentication
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean // Inorder to implement the database authentication
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder()); // PasswordEncoder
        provider.setUserDetailsService(customDetailsService);
        return provider;
    }

    // @Bean // UsernamePasswordAuthenticationFilter >>> To Verify User Credentials
    // public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder)
    // {
    // UserDetails user1 =
    // User.withDefaultPasswordEncoder().username("Aue").password("aue12345").roles("USER")
    // .build();
    // UserDetails user2 =
    // User.withDefaultPasswordEncoder().username("Cool").password("cool12345").roles("admin")
    // .build();
    // return new InMemoryUserDetailsManager(user1, user2);
    // }
}
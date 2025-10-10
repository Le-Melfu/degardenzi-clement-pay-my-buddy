package com.paymybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
                requestHandler.setCsrfRequestAttributeName("_csrf");

                http.authorizeHttpRequests(authz -> authz
                                .requestMatchers("/register", "/csrf").permitAll()
                                .requestMatchers("/login").permitAll()
                                .anyRequest().authenticated())
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                                .csrfTokenRequestHandler(requestHandler))
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionFixation().migrateSession()
                                                .sessionConcurrency(concurrency -> concurrency
                                                                .maximumSessions(1)
                                                                .maxSessionsPreventsLogin(false)))
                                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                final List<String> FRONTEND_URLS = Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173",
                                "http://localhost:4173", "http://127.0.0.1:4173");
                configuration.setAllowedOriginPatterns(FRONTEND_URLS);

                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

                configuration.setAllowedHeaders(Arrays.asList(
                                "Content-Type",
                                "Accept",
                                "X-Requested-With",
                                "X-XSRF-TOKEN"));

                configuration.setAllowCredentials(true);

                configuration.setExposedHeaders(Arrays.asList(
                                "Content-Type",
                                "X-XSRF-TOKEN"));

                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        /**
         * Configure SameSite attribute for CSRF cookie
         * Note: The Secure attribute is managed by application.properties
         */
        @Bean
        public CookieSameSiteSupplier cookieSameSiteSupplier() {
                return CookieSameSiteSupplier.ofLax().whenHasName("XSRF-TOKEN");
        }
}

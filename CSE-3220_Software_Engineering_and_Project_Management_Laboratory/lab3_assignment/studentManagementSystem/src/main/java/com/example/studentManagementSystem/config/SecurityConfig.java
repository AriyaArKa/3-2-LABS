package com.example.studentManagementSystem.config;

import com.example.studentManagementSystem.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration
 * 
 * ====================================================================
 * AUTHENTICATION vs AUTHORIZATION - Key Concepts
 * ====================================================================
 * 
 * AUTHENTICATION: "Who are you?"
 * - The process of verifying the identity of a user
 * - User provides credentials (username/password)
 * - System validates these credentials against stored data
 * - Example: Logging in with username and password
 * 
 * AUTHORIZATION: "What are you allowed to do?"
 * - The process of determining what actions a user can perform
 * - Happens AFTER authentication
 * - Based on user's role or permissions
 * - Example: Teacher can manage students, but student cannot
 * 
 * In this application:
 * - Authentication: Login with username/password
 * - Authorization: ROLE_TEACHER can access /students/**, /teachers/**, etc.
 *                  ROLE_STUDENT can only access their own profile and courses
 * ====================================================================
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Password encoder using BCrypt algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider using our custom user details service
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Security filter chain - configures HTTP security
     * Implements role-based access control
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for simplicity (enable in production)
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - accessible by everyone
                .requestMatchers("/", "/login", "/about", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Student-only endpoints
                .requestMatchers("/students/profile", "/students/my-courses", "/students/my-department").hasRole("STUDENT")
                .requestMatchers("/students/enroll/**", "/students/drop/**").hasRole("STUDENT")
                
                // Teacher-only endpoints - CRUD operations
                .requestMatchers("/students/**").hasRole("TEACHER")
                .requestMatchers("/teachers/**").hasRole("TEACHER")
                .requestMatchers("/departments/**").hasRole("TEACHER")
                .requestMatchers("/courses/**").hasRole("TEACHER")
                
                // Dashboard accessible by both roles
                .requestMatchers("/dashboard").hasAnyRole("STUDENT", "TEACHER")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure form login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // Configure logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // Handle access denied
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            
            // Set authentication provider
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}

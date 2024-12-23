package com.example.demo.configuration;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.demo.service.UserService;
import com.example.demo.filter.JWTAuthFilter;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JWTAuthFilter authFilter;
    public SecurityConfig(JWTAuthFilter authFilter) {
        this.authFilter = authFilter;
    }
    // User Creation
    @Bean
    public UserDetailsService userDetailsService(UserRepository repository, PasswordEncoder passwordEncoder, CourseRepository courseRepository) {
        return new UserService(repository, passwordEncoder, courseRepository);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        return http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        .requestMatchers(
                                "/auth/hello", "/auth/profile", "/auth/list", "/auth/{id}", "/api/courses",
                                "/{courseId}", "/api/lessons", "/api/enrollments", "/course/{courseId}",
                                "/api/attendance", "/generate-otp", "/submit-otp", "/lesson/{lessonId}/marked","/api/quizzes" , "/api/quizzes/{quizId}/questions", "/api/quizzes/{quizId}/attemptQuiz", "/api/quizzes/{quizId}/submitQuiz"
                                ,"/api/assignments/create","/api/assignments/{assignmentId}/submit",
                                "/api/assignments/assignments","/api/assignments/submissions",
                                "/api/assignments/{assignmentId}/feedback"
                        ).authenticated()
                )
                .httpBasic(withDefaults()).csrf((csrf) -> csrf.disable())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
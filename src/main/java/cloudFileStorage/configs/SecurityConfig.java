package cloudFileStorage.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/css/**", "/images/**").permitAll()
                .requestMatchers("/", "/auth/signIn", "/auth/signUp", "/auth/success").permitAll()
                .anyRequest().hasAuthority("USER"));

        http.formLogin(formLogin -> formLogin
                .loginPage("/auth/signIn")
                .loginProcessingUrl("/process_signIn")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/signIn?error"));

        http.logout(logout -> logout
                .logoutUrl("/signOut")
                .logoutSuccessUrl("/"));

        http.rememberMe(Customizer.withDefaults());

//        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

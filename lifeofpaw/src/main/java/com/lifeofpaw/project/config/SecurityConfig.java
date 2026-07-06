package com.lifeofpaw.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(withDefaults())
            .authorizeHttpRequests(auth -> auth
                
                // 1. PUBLIC ZONE (Keeping your working rules)
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/animals/all", "/api/animals/available", "/api/animals/search", "/api/animals/public/org/**", "/api/animals/get-particular/**", "/api/users/get-particular/**").permitAll()
                .requestMatchers("/api/orgs/all").permitAll()
                .requestMatchers("/api/social/community-feed").permitAll()
                .requestMatchers("/uploads/**").permitAll()

                // 2. ADMIN ONLY ZONE (Keeping your working rules)
                .requestMatchers("/api/orgs/pending").hasAuthority("ROLE_admin")
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_admin")
                .requestMatchers("/api/users/all").hasAuthority("ROLE_admin")//write this always before in order
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ROLE_admin")
                .requestMatchers(HttpMethod.DELETE, "/api/orgs/**").hasAuthority("ROLE_admin")
                .requestMatchers("/api/orgs/*/verify").hasAuthority("ROLE_admin")
                .requestMatchers("/api/adoptions/all").hasAuthority("ROLE_admin")

                // 3. NGO & ADMIN ZONE (Keeping your working rules)
                .requestMatchers("/api/ngo/**").hasAnyAuthority("ROLE_org", "ROLE_admin")
                .requestMatchers("/api/animals/add").hasAnyAuthority("ROLE_org", "ROLE_admin")
                .requestMatchers(HttpMethod.PUT, "/api/animals/**").hasAnyAuthority("ROLE_org", "ROLE_admin")
                .requestMatchers(HttpMethod.DELETE, "/api/animals/**").hasAnyAuthority("ROLE_org", "ROLE_admin")
                .requestMatchers("/api/adoptions/update-status").hasAnyAuthority("ROLE_org", "ROLE_admin")
                .requestMatchers("/api/animals/{animalId}/add-image").hasAnyAuthority("ROLE_org", "ROLE_admin")
                .requestMatchers(HttpMethod.DELETE, "/api/animals/{animalId}/remove-image").hasAnyAuthority("ROLE_org", "ROLE_admin")
                
                // 4. USER ACTIONS (Keeping your working rules)
                .requestMatchers("/api/adoptions/apply").authenticated()
                .requestMatchers("/api/adoptions/user/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/orgs/add").authenticated()
                
                // 5. FINANCE ZONE (Keeping your working rules)
                .requestMatchers("/api/finance/donate").authenticated()
                .requestMatchers("/api/finance/my-history").authenticated()
                .requestMatchers("/api/finance/user/**").authenticated()
                .requestMatchers("/api/finance/ngo/**").hasAnyAuthority("ROLE_org", "ROLE_admin")
                .requestMatchers("/api/finance/admin/**").hasAuthority("ROLE_admin")

                // 6. SOCIAL & COMMUNITY ZONE (New Additions)
                .requestMatchers("/api/social/history/**").authenticated()
                .requestMatchers("/api/social/messages/send-to-admin").authenticated()
                .requestMatchers("/api/social/messages/admin/support-feed").hasAuthority("ROLE_admin")
                .requestMatchers("/api/social/messages/admin/reply/**").hasAuthority("ROLE_admin") 
                .requestMatchers("/api/social/reviews/add").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/social/reviews/**").hasAnyAuthority("ROLE_user","ROLE_org", "ROLE_admin") // NGO/Admin can delete
                .requestMatchers("/api/social/messages/**").authenticated() 
                .requestMatchers("/api/social/post-to-feed").authenticated()
                .requestMatchers("/api/social/reviews/org/**").permitAll() // Let everyone see reviews
                .requestMatchers("/api/social/reviews/all").hasAuthority("ROLE_admin")
                .requestMatchers("/api/finance/admin/**").hasAuthority("ROLE_admin")

                
                // 7. SAFETY NET (Always LAST)
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults()); 
            
        return http.build();
    }
}
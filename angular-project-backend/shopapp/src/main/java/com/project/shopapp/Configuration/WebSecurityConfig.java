package com.project.shopapp.Configuration;

import com.project.shopapp.Filter.JwtFilter;
import com.project.shopapp.Models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtFilter jwtFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                //nếu không có token hợp lệ(chưa đăng nhập ) quy trình lần lượt như sau:
                // vào jwtFilter kiểm tra xem endpoint có cần token hay không
                // nếu cần thì nhảy vào UsernamePasswordAuthenticationFilter bắt đầu login
                // và dùng authenticationManager sau đó trả về token và các request tiếp theo nhảy về jwtFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request ->
                {
                    request.requestMatchers(
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/users/register", apiPrefix)
                            ).permitAll()
                            .requestMatchers(POST,String.format("%s/users/details/**",apiPrefix)).authenticated()
                            .requestMatchers(GET,String.format("%s/categories/**",apiPrefix)).permitAll()
                            .requestMatchers(GET,String.format("%s/products/**",apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/products/images/**",apiPrefix)).permitAll()
                            //order
                            .requestMatchers(POST, String.format("%s/order/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(GET, String.format("%s/order/**", apiPrefix)).hasAnyRole( Role.ADMIN, Role.USER)
                            .requestMatchers(GET, String.format("%s/order/user/**", apiPrefix)).hasAnyRole( Role.ADMIN)
                            .requestMatchers(PUT, String.format("%s/order/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/order/**", apiPrefix)).hasRole(Role.ADMIN)
                            //orderDetails
                            .requestMatchers(POST, String.format("%s/order_detail/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(GET, String.format("%s/order_detail/order/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("%s/order_detail/**", apiPrefix)).hasAnyRole( Role.ADMIN, Role.USER)
                            .requestMatchers(PUT, String.format("%s/order_detail/**", apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(DELETE, String.format("%s/order_detail/**", apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            //categories
                            .requestMatchers(POST, String.format("%s/categories/**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("%s/categories/**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/categories/**",apiPrefix)).hasRole(Role.ADMIN)
                            //product
                            .requestMatchers(POST, String.format("%s/products/**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("%s/products/**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/products/**",apiPrefix)).hasRole(Role.ADMIN);
                            //.anyRequest().authenticated();
                });
            httpSecurity.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
                @Override
                public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("*"));//List.of(immutableList)
                    configuration.setAllowedMethods(Arrays.asList("GET","PUT","POST","PATCH","DELETE","OPTIONS"));
                    configuration.setAllowedHeaders(Arrays.asList("authorization","content-Type","x-auth-token"));//Arrays.asList(mutable)
                    configuration.setExposedHeaders(List.of("x-auth-token"));
                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**",configuration);
                    httpSecurityCorsConfigurer.configurationSource(source);
                }
            });
        return httpSecurity.build();
    }
}

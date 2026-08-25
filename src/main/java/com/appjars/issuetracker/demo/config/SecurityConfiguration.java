/*-
 * #%L
 * Issue Tracker AppJars - Demo
 * %%
 * Copyright (C) 2023 - 2026 AppJars
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.appjars.issuetracker.demo.config;

import static com.vaadin.flow.spring.security.VaadinSecurityConfigurer.vaadin;

import com.appjars.issuetracker.demo.service.SecurityPasswordEncoder;
import com.appjars.issuetracker.demo.views.LoginView;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfiguration {

  public static final String LOGOUT_URL = "/";

  final SecurityPasswordEncoder securityPasswordEncoder;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // Public paths must be declared BEFORE calling vaadin(),
    // as vaadin() adds a final anyRequest matcher.
    http.authorizeHttpRequests(
        (authorize) ->
            authorize
                .requestMatchers("/login*")
                .permitAll()
                .requestMatchers("/login?error=.*")
                .permitAll()
                .requestMatchers("/offline-stub.html")
                .permitAll()
                .requestMatchers("/offline-page.html")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/*.png", "/*.css", "/images/**", "/icons/**")
                .permitAll());

    http.with(vaadin(), configurer -> configurer.loginView(LoginView.class, LOGOUT_URL));

    return http.build();
  }
}

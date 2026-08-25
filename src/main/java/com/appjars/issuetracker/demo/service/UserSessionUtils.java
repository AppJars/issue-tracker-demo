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
package com.appjars.issuetracker.demo.service;

import com.appjars.issuetracker.business.service.LoggedInUsernameProvider;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSessionUtils implements LoggedInUsernameProvider {

  final CustomUserDetailService customUserDetailService;
  final AuthenticationContext authenticationContext;

  @Transactional
  public Optional<UserDetails> get() {
    return authenticationContext
        .getAuthenticatedUser(UserDetails.class)
        .map(userDetails -> customUserDetailService.loadUserByUsername(userDetails.getUsername()));
  }

  public void logout() {
    authenticationContext.logout();
  }

  @Transactional
  public boolean isAdmin() {
    return get()
        .map(
            userDetails ->
                userDetails.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_IT_ADMIN".equals(authority.getAuthority())))
        .orElse(false);
  }

  @Override
  @Transactional
  public Optional<String> getLoggedInUsername() {
    Optional<UserDetails> ud = this.get();

    if (!ud.isEmpty()) {
      return Optional.of(ud.get().getUsername());
    } else {
      return Optional.empty();
    }
  }
}

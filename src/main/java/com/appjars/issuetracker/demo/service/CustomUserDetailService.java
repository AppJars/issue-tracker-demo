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

import com.appjars.issuetracker.business.service.UserService;
import com.appjars.issuetracker.model.UserDto;
import com.flowingcode.backendcore.model.ConstraintBuilder;
import com.flowingcode.backendcore.model.QuerySpec;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomUserDetailService implements UserDetailsService {

  final UserService userService;

  public CustomUserDetailService(@Qualifier("ITUserServiceImpl") UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    QuerySpec filter = new QuerySpec();
    filter.addConstraint(ConstraintBuilder.of("login").equal(username));

    UserDto user =
        userService
            .filterWithSingleResult(filter)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String[] roles = user.isAdmin() ? new String[] {"IT_USER", "IT_ADMIN"} : new String[] {"IT_USER"};

    return org.springframework.security.core.userdetails.User.withUsername(user.getLogin())
        .password(user.getHashedPassword())
        .roles(roles)
        .build();
  }
}

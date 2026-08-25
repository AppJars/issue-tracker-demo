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
package com.appjars.issuetracker.demo.data;

import com.appjars.issuetracker.demo.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Runs part of the seeding under a given account.
 *
 * <p>The appjar reads the acting user from the security context, and a {@code CommandLineRunner}
 * has none — so seeding needs one installed. Beyond that bare requirement, seeding each issue as
 * the person who would really have filed and worked on it is what makes the resulting data worth
 * showing: the history tab attributes every change to the right member instead of to {@code
 * admin}, and the appjar's "watch the issues I contributed to" preference stops turning the demo
 * account into a watcher of every single issue.
 */
@Component
@RequiredArgsConstructor
class DemoIdentity {

  private final CustomUserDetailService userDetailService;

  /** Runs {@code action} authenticated as {@code login}, restoring the previous context after. */
  void runAs(String login, Runnable action) {
    SecurityContext previous = SecurityContextHolder.getContext();
    try {
      UserDetails user = userDetailService.loadUserByUsername(login);
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(
          new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
      SecurityContextHolder.setContext(context);
      action.run();
    } finally {
      SecurityContextHolder.setContext(previous);
    }
  }
}

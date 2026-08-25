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

import com.appjars.issuetracker.business.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Populates the demo with a realistic dataset: a fully configured project with its own workflow,
 * five users across five roles, several trackers, a roadmap, an issue backlog with logged time,
 * and a Gantt with actual precedencies.
 *
 * <p>Runs after {@link com.appjars.issuetracker.demo.config.DataInitializer}, which bootstraps the
 * admin account and the appjar's minimum settings, and only when the demo dataset is not already
 * there — so restarting the application against an existing database is a no-op and anything a
 * visitor changed survives.
 *
 * <p><b>Free licence limits shape this dataset.</b> The demo intentionally ships without a licence
 * so it never expires, which caps it at 5 users, 5 open issues and 10 time entries created per
 * day. Those limits are enforced in the business layer, so this seeder is subject to them exactly
 * like the UI is. Two consequences worth knowing before editing the seed data:
 *
 * <ul>
 *   <li>The user count is a hard ceiling: 5 including {@code admin}, and going over also blocks
 *       editing users.
 *   <li>Only issues in a non-closed status count, so the backlog can be as large as we like as
 *       long as few issues stay open. Time entries are counted by creation timestamp, not by the
 *       date the time was spent on, so this seeder backdates each row right after writing it —
 *       which is what we want for the data to look real anyway, and incidentally keeps the daily
 *       counter at zero.
 * </ul>
 */
@Component
@Order(20)
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataSeeder.class);

  /**
   * The project carrying the full demo dataset. Its presence is what marks the database as already
   * seeded, and it is the project the landing page sends project-scoped tours to.
   */
  public static final String MAIN_PROJECT_IDENTIFIER = "apollo-platform";

  private final ProjectService projectService;
  private final DemoIdentity identity;
  private final DemoCatalogSeeder catalogSeeder;
  private final DemoProjectSeeder projectSeeder;

  @Override
  public void run(String... args) {
    if (projectService.findByIdentifier(MAIN_PROJECT_IDENTIFIER).isPresent()) {
      LOGGER.info("Demo dataset already present, skipping seeding.");
      return;
    }

    try {
      long startedAt = System.currentTimeMillis();
      // The catalog and the projects themselves are administrative work, so they are created as
      // admin; the issues then switch identity per author (see DemoProjectSeeder).
      identity.runAs(DemoData.Names.USER_ADMIN, () -> {
        DemoData data = catalogSeeder.seed();
        projectSeeder.seed(data);
      });
      LOGGER.info("Demo dataset seeded in {} ms.", System.currentTimeMillis() - startedAt);
    } catch (Exception e) {
      // A half-seeded database is still usable and the guard above keys on the main project, so a
      // failure here must not stop the application from starting: the demo degrades to whatever
      // DataInitializer left behind instead of refusing to boot.
      LOGGER.error("Could not seed the demo dataset. The demo will start with base data only.", e);
    }
  }
}

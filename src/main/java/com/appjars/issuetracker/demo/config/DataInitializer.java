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

import java.sql.Connection;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

/**
 * Bootstraps the minimum the appjar needs before it can serve a request: the {@code admin} account
 * and the built-in Administrator role plus the default list settings carried by {@code data.sql}.
 *
 * <p>The demo dataset a visitor actually sees is created afterwards by
 * {@link com.appjars.issuetracker.demo.data.DemoDataSeeder}, which is ordered to run after this
 * one.
 */
@Component
@Order(10)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);
  private static final String DEFAULT_ADMIN_SALT = "401d45557d4daee86ba50ef475c1434b";
  private static final String ADMINISTRATOR_ROLE = "Administrator";
  private static final String REDMINE_ADMIN_TRANSFORMATION_SCRIPT = "redmine-admin-transformation.sql";
  private static final String DEMO_REMAINING_DATA_SCRIPT = "data.sql";

  private final JdbcTemplate jdbcTemplate;
  private final DataSource dataSource;

  @Override
  public void run(String... args) throws Exception {
    // Keyed on the Administrator role this script inserts rather than on a table being empty: the
    // roles table is never empty, because Redmine's own migrations seed the built-in "Non member"
    // and "Anonymous" roles before this application ever connects.
    Integer administratorRoleCount = jdbcTemplate.queryForObject(
        "SELECT count(*) FROM public.roles WHERE name = ?", Integer.class, ADMINISTRATOR_ROLE);
    boolean alreadyBootstrapped = administratorRoleCount != null && administratorRoleCount > 0;

    if (alreadyBootstrapped) {
      LOGGER.info("Skipping demo seed scripts: the {} role is already present.",
          ADMINISTRATOR_ROLE);
      return;
    }

    Integer defaultSaltCount =
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM public.users WHERE \"type\" = 'User' AND salt = ?",
            Integer.class,
            DEFAULT_ADMIN_SALT);

    boolean defaultSaltExists = defaultSaltCount != null && defaultSaltCount > 0;

    try (Connection connection = dataSource.getConnection()) {
      if (!defaultSaltExists) {
        executeScript(connection, REDMINE_ADMIN_TRANSFORMATION_SCRIPT);
      }

      executeScript(connection, DEMO_REMAINING_DATA_SCRIPT);
    } catch (Exception e) {
      LOGGER.error("Error executing demo seed scripts.", e);
    }
  }

  private void executeScript(Connection connection, String scriptName) {
    ScriptUtils.executeSqlScript(connection, new ClassPathResource(scriptName));
    LOGGER.info("{} executed successfully.", scriptName);
  }
}

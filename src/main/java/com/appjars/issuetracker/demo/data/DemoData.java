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

import com.appjars.issuetracker.model.CustomFieldDto;
import com.appjars.issuetracker.model.EnumerationDto;
import com.appjars.issuetracker.model.IssueStatusDto;
import com.appjars.issuetracker.model.RoleDto;
import com.appjars.issuetracker.model.TrackerDto;
import com.appjars.issuetracker.model.UserDto;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * References to everything {@link DemoCatalogSeeder} creates, handed to {@link DemoProjectSeeder}
 * so it can wire projects, issues and time entries to them without querying by name again.
 *
 * <p>Every map is keyed by the stable identifiers declared in {@link Names}, never by display
 * text, so renaming a label in the seed data does not break the lookups.
 */
public class DemoData {

  /**
   * Stable keys for the seeded catalog entries. Display names live in the seeder; these are what
   * the rest of the seeding code refers to.
   */
  public static final class Names {

    // Issue statuses. Only IN_PROGRESS, IN_REVIEW and BLOCKED are open (see DemoProjectSeeder for
    // why the number of open issues is capped).
    public static final String STATUS_NEW = "New";
    public static final String STATUS_IN_PROGRESS = "In progress";
    public static final String STATUS_IN_REVIEW = "In review";
    public static final String STATUS_BLOCKED = "Blocked";
    public static final String STATUS_CLOSED = "Closed";
    public static final String STATUS_REJECTED = "Rejected";

    // Trackers.
    public static final String TRACKER_BUG = "Bug";
    public static final String TRACKER_FEATURE = "Feature";
    public static final String TRACKER_TASK = "Task";
    public static final String TRACKER_SUPPORT = "Support";
    public static final String TRACKER_EPIC = "Epic";

    // Issue priorities.
    public static final String PRIORITY_LOW = "Low";
    public static final String PRIORITY_NORMAL = "Normal";
    public static final String PRIORITY_HIGH = "High";
    public static final String PRIORITY_URGENT = "Urgent";
    public static final String PRIORITY_IMMEDIATE = "Immediate";

    // Time entry activities.
    public static final String ACTIVITY_DESIGN = "Design";
    public static final String ACTIVITY_DEVELOPMENT = "Development";
    public static final String ACTIVITY_CODE_REVIEW = "Code review";
    public static final String ACTIVITY_QA = "QA";
    public static final String ACTIVITY_DOCUMENTATION = "Documentation";
    public static final String ACTIVITY_MEETING = "Meeting";
    public static final String ACTIVITY_SUPPORT = "Support";

    // Roles.
    public static final String ROLE_ADMINISTRATOR = "Administrator";
    public static final String ROLE_PROJECT_MANAGER = "Project Manager";
    public static final String ROLE_DEVELOPER = "Developer";
    public static final String ROLE_QA = "QA Engineer";
    public static final String ROLE_REPORTER = "Reporter";

    // Users, by login.
    public static final String USER_ADMIN = "admin";
    public static final String USER_PM = "mrivas";
    public static final String USER_DEV1 = "dsantos";
    public static final String USER_DEV2 = "lchen";
    public static final String USER_QA = "pnovak";

    // Custom fields.
    public static final String CF_SEVERITY = "Severity";
    public static final String CF_CUSTOMER = "Customer";
    public static final String CF_REGRESSION = "Regression";
    public static final String CF_STORY_POINTS = "Story points";

    private Names() {}
  }

  final Map<String, IssueStatusDto> statuses = new LinkedHashMap<>();
  final Map<String, TrackerDto> trackers = new LinkedHashMap<>();
  final Map<String, EnumerationDto> priorities = new LinkedHashMap<>();
  final Map<String, EnumerationDto> activities = new LinkedHashMap<>();
  final Map<String, RoleDto> roles = new LinkedHashMap<>();
  final Map<String, UserDto> users = new LinkedHashMap<>();
  final Map<String, CustomFieldDto> customFields = new LinkedHashMap<>();

  public IssueStatusDto status(String name) {
    return require(statuses, name, "issue status");
  }

  public TrackerDto tracker(String name) {
    return require(trackers, name, "tracker");
  }

  public EnumerationDto priority(String name) {
    return require(priorities, name, "priority");
  }

  public EnumerationDto activity(String name) {
    return require(activities, name, "activity");
  }

  public RoleDto role(String name) {
    return require(roles, name, "role");
  }

  public UserDto user(String login) {
    return require(users, login, "user");
  }

  public CustomFieldDto customField(String name) {
    return require(customFields, name, "custom field");
  }

  private static <T> T require(Map<String, T> map, String key, String what) {
    T value = map.get(key);
    if (value == null) {
      throw new IllegalStateException("Demo seed data is missing the " + what + " '" + key + "'");
    }
    return value;
  }
}

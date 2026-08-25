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

import static com.appjars.issuetracker.demo.data.DemoData.Names.ACTIVITY_CODE_REVIEW;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ACTIVITY_DESIGN;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ACTIVITY_DEVELOPMENT;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ACTIVITY_DOCUMENTATION;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ACTIVITY_MEETING;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ACTIVITY_QA;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ACTIVITY_SUPPORT;
import static com.appjars.issuetracker.demo.data.DemoData.Names.CF_CUSTOMER;
import static com.appjars.issuetracker.demo.data.DemoData.Names.CF_REGRESSION;
import static com.appjars.issuetracker.demo.data.DemoData.Names.CF_SEVERITY;
import static com.appjars.issuetracker.demo.data.DemoData.Names.CF_STORY_POINTS;
import static com.appjars.issuetracker.demo.data.DemoData.Names.PRIORITY_HIGH;
import static com.appjars.issuetracker.demo.data.DemoData.Names.PRIORITY_IMMEDIATE;
import static com.appjars.issuetracker.demo.data.DemoData.Names.PRIORITY_LOW;
import static com.appjars.issuetracker.demo.data.DemoData.Names.PRIORITY_NORMAL;
import static com.appjars.issuetracker.demo.data.DemoData.Names.PRIORITY_URGENT;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ROLE_DEVELOPER;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ROLE_PROJECT_MANAGER;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ROLE_QA;
import static com.appjars.issuetracker.demo.data.DemoData.Names.ROLE_REPORTER;
import static com.appjars.issuetracker.demo.data.DemoData.Names.STATUS_BLOCKED;
import static com.appjars.issuetracker.demo.data.DemoData.Names.STATUS_CLOSED;
import static com.appjars.issuetracker.demo.data.DemoData.Names.STATUS_IN_PROGRESS;
import static com.appjars.issuetracker.demo.data.DemoData.Names.STATUS_IN_REVIEW;
import static com.appjars.issuetracker.demo.data.DemoData.Names.STATUS_NEW;
import static com.appjars.issuetracker.demo.data.DemoData.Names.STATUS_REJECTED;
import static com.appjars.issuetracker.demo.data.DemoData.Names.TRACKER_BUG;
import static com.appjars.issuetracker.demo.data.DemoData.Names.TRACKER_EPIC;
import static com.appjars.issuetracker.demo.data.DemoData.Names.TRACKER_FEATURE;
import static com.appjars.issuetracker.demo.data.DemoData.Names.TRACKER_SUPPORT;
import static com.appjars.issuetracker.demo.data.DemoData.Names.TRACKER_TASK;
import static com.appjars.issuetracker.demo.data.DemoData.Names.USER_ADMIN;
import static com.appjars.issuetracker.demo.data.DemoData.Names.USER_DEV1;
import static com.appjars.issuetracker.demo.data.DemoData.Names.USER_DEV2;
import static com.appjars.issuetracker.demo.data.DemoData.Names.USER_PM;
import static com.appjars.issuetracker.demo.data.DemoData.Names.USER_QA;

import com.appjars.issuetracker.business.service.IssueCategoryService;
import com.appjars.issuetracker.business.service.IssueRelationService;
import com.appjars.issuetracker.business.service.IssueService;
import com.appjars.issuetracker.business.service.MemberService;
import com.appjars.issuetracker.business.service.ProjectService;
import com.appjars.issuetracker.business.service.QueryService;
import com.appjars.issuetracker.business.service.TimeEntryService;
import com.appjars.issuetracker.business.service.VersionService;
import com.appjars.issuetracker.business.service.WatcherService;
import com.appjars.issuetracker.model.CustomFieldDto;
import com.appjars.issuetracker.model.IssueCategoryDto;
import com.appjars.issuetracker.model.IssueDto;
import com.appjars.issuetracker.model.IssueRelationDto;
import com.appjars.issuetracker.model.JournalDto;
import com.appjars.issuetracker.model.ProjectDto;
import com.appjars.issuetracker.model.ProjectStatus;
import com.appjars.issuetracker.model.QueryDto;
import com.appjars.issuetracker.model.enums.QueryType;
import com.appjars.issuetracker.model.TimeEntryDto;
import com.appjars.issuetracker.model.UserDto;
import com.appjars.issuetracker.model.VersionDto;
import com.appjars.issuetracker.model.WatcherDto;
import com.appjars.issuetracker.model.enums.EnabledModule;
import com.appjars.issuetracker.model.enums.IssueRelationType;
import com.appjars.issuetracker.model.enums.VersionStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Seeds the demo's project content: three projects, their members, roadmap and categories, an
 * issue backlog with subtasks and precedencies, and roughly three months of logged time.
 *
 * <p>Two constraints imposed by the free licence shape what is created here (see
 * {@link DemoDataSeeder} for the full picture):
 *
 * <ul>
 *   <li><b>At most {@link #MAX_OPEN_ISSUES} issues stay in a non-closed status.</b> The business
 *       layer refuses to create an issue once five are open, and disables editing above that, so
 *       the backlog is deliberately a mostly-finished project: everything is closed or rejected
 *       except a handful of issues assigned to {@code admin}, which is also what keeps My Page
 *       from looking empty. The remaining headroom is what lets a visitor create issues.
 *   <li><b>Timestamps are rewritten right after each row is written.</b> The services stamp
 *       {@code created_on} with the current instant, which would make three months of history look
 *       like it all happened at startup — and, for time entries, would exhaust the ten-per-day
 *       allowance after ten rows. Backdating solves both at once.
 * </ul>
 */
@Component
@RequiredArgsConstructor
class DemoProjectSeeder {

  /** How many issues are left in an open status; see the class javadoc. */
  private static final int MAX_OPEN_ISSUES = 3;

  /** Fixed seed: the dataset must look varied but be identical on every machine. */
  private static final long RANDOM_SEED = 20260815L;

  private static final String SUB_PROJECT_IDENTIFIER = "apollo-mobile";
  private static final String LEGACY_PROJECT_IDENTIFIER = "legacy-portal";

  private static final Set<EnabledModule> FULL_MODULES = EnumSet.of(EnabledModule.OVERVIEW,
      EnabledModule.ACTIVITY, EnabledModule.ISSUES, EnabledModule.TIME_ENTRIES,
      EnabledModule.GANTT, EnabledModule.SETTINGS, EnabledModule.SEARCH);

  /** Fictional accounts used as custom field values on customer-facing issues. */
  private static final List<String> CUSTOMERS =
      List.of("Northwind", "Contoso", "Initech", "Umbrella Logistics", "Globex");

  private static final List<Integer> STORY_POINTS = List.of(1, 2, 3, 5, 5, 8, 8, 13);

  private final DemoIdentity identity;
  private final ProjectService projectService;
  private final MemberService memberService;
  private final VersionService versionService;
  private final IssueCategoryService issueCategoryService;
  private final IssueService issueService;
  private final IssueRelationService issueRelationService;
  private final TimeEntryService timeEntryService;
  private final WatcherService watcherService;
  private final QueryService queryService;
  private final JdbcTemplate jdbcTemplate;

  private final Random random = new Random(RANDOM_SEED);

  void seed(DemoData data) {
    LocalDate today = LocalDate.now();

    ProjectDto main = createProject("Apollo Platform", DemoDataSeeder.MAIN_PROJECT_IDENTIFIER,
        "Customer-facing platform: accounts, billing and the reporting back office. "
            + "This is the project to look at first — it carries the full demo dataset.",
        null, ProjectStatus.ACTIVE, data);
    ProjectDto mobile = createProject("Apollo Mobile", SUB_PROJECT_IDENTIFIER,
        "Companion mobile application. A subproject, so it shows how the project tree and the "
            + "subproject search scope behave.",
        main, ProjectStatus.ACTIVE, data);
    ProjectDto legacy = createProject("Legacy Portal", LEGACY_PROJECT_IDENTIFIER,
        "Retired self-service portal, kept read-only for reference.", null, ProjectStatus.CLOSED,
        data);

    seedMembers(main, mobile, legacy, data);

    Map<String, VersionDto> versions = seedVersions(main, mobile, today);
    Map<String, IssueCategoryDto> categories = seedCategories(main, mobile, data);

    seedMainProjectIssues(main, data, versions, categories, today);
    seedMobileIssues(mobile, data, versions, categories, today);
    seedLegacyIssues(legacy, data, today);

    seedSavedQueries(main, data, versions);

    assertOpenIssueBudget();
  }

  /**
   * Public saved queries, one of which becomes the main project's default.
   *
   * <p>This is not decoration: the issue list defaults to showing only open issues, faithfully to
   * Redmine, and this demo deliberately keeps just three issues open so that visitors can still
   * create their own. Without a default query the main list would therefore open on three rows and
   * the whole backlog would look empty. A project's default query overrides that filter, so
   * "All issues" is what a visitor actually lands on — and the other two double as examples of the
   * saved query feature itself.
   */
  private void seedSavedQueries(ProjectDto main, DemoData data, Map<String, VersionDto> versions) {
    UserDto owner = data.user(USER_ADMIN);
    QueryDto allIssues = saveQuery(main, "All issues", owner, null);
    saveQuery(main, "Bugs", owner,
        filterYaml("tracker_id", "=", String.valueOf(data.tracker(TRACKER_BUG).getId())));
    saveQuery(main, "Delivered in 1.1", owner,
        filterYaml("fixed_version_id", "=", String.valueOf(versions.get("1.1").getId())));

    main.setDefaultIssueQuery(allIssues);
    projectService.update(main, Map.of());
  }

  private QueryDto saveQuery(ProjectDto project, String name, UserDto owner, String filters) {
    QueryDto query = QueryDto.builder().project(project).name(name).type(QueryType.ISSUE)
        // Every query is owned by a user, and the column is not nullable. Visibility is what makes
        // these reachable by everyone, not the owner.
        .user(owner)
        // 2 is the "visible to any user" option offered by the save dialog, so these show up for
        // every visitor rather than only for the account that happens to be signed in.
        .visibility(2)
        .filters(filters == null ? "--- {}" : filters)
        .build();
    query.setId(queryService.save(query));
    return query;
  }

  /** Builds the Redmine-style YAML a saved query stores its filters in. */
  private static String filterYaml(String key, String operator, String value) {
    return "---\n" + key + ":\n  :operator: \"" + operator + "\"\n  :values:\n  - '" + value + "'\n";
  }

  /**
   * Fails loudly if the seed data drifted past the open-issue budget. Without this, adding an
   * issue in an open status would surface much later and much more confusingly, as the business
   * layer rejecting the sixth open issue halfway through seeding — or, worse, as a demo where the
   * visitor cannot create or edit anything.
   */
  private void assertOpenIssueBudget() {
    long open = issueService.countOpenIssues();
    if (open > MAX_OPEN_ISSUES) {
      throw new IllegalStateException("The demo seed data leaves " + open
          + " issues open, but the free licence only leaves room for " + MAX_OPEN_ISSUES
          + ". Close some of the seeded issues or the demo will not let visitors create their own.");
    }
  }

  // ---------------------------------------------------------------- projects and members

  private ProjectDto createProject(String name, String identifier, String description,
      ProjectDto parent, ProjectStatus status, DemoData data) {
    ProjectDto project = ProjectDto.builder().name(name).identifier(identifier)
        .description(description).homepage("").publicProject(true).parent(parent).status(status)
        .inheritMembers(false).enabledModules(EnumSet.copyOf(FULL_MODULES))
        .trackers(new HashSet<>(data.trackers.values())).build();
    project.setId(projectService.save(project, Map.of()));
    return project;
  }

  /**
   * Memberships. The same person deliberately holds different roles in different projects — Lin
   * Chen develops on the platform but runs QA on mobile — so the members panel and the role-based
   * permission checks have something non-trivial to show.
   */
  private void seedMembers(ProjectDto main, ProjectDto mobile, ProjectDto legacy, DemoData data) {
    UserDto pm = data.user(USER_PM);
    UserDto dev1 = data.user(USER_DEV1);
    UserDto dev2 = data.user(USER_DEV2);
    UserDto qa = data.user(USER_QA);
    UserDto admin = data.user(USER_ADMIN);

    memberService.save(pm, main, Set.of(data.role(ROLE_PROJECT_MANAGER)));
    memberService.save(dev1, main, Set.of(data.role(ROLE_DEVELOPER)));
    memberService.save(dev2, main, Set.of(data.role(ROLE_DEVELOPER)));
    memberService.save(qa, main, Set.of(data.role(ROLE_QA)));
    memberService.save(admin, main, Set.of(data.role(ROLE_PROJECT_MANAGER)));

    memberService.save(pm, mobile, Set.of(data.role(ROLE_PROJECT_MANAGER)));
    memberService.save(dev1, mobile, Set.of(data.role(ROLE_DEVELOPER)));
    memberService.save(dev2, mobile, Set.of(data.role(ROLE_QA)));

    memberService.save(qa, legacy, Set.of(data.role(ROLE_REPORTER)));
  }

  /**
   * The roadmap: one released version, the one currently being worked on, and one still being
   * planned, so the roadmap screen shows the three states side by side.
   */
  private Map<String, VersionDto> seedVersions(ProjectDto main, ProjectDto mobile,
      LocalDate today) {
    Map<String, VersionDto> versions = new LinkedHashMap<>();
    versions.put("1.0", saveVersion(main, "1.0 Foundation",
        "Accounts, authentication and the billing core.", today.minusDays(75),
        VersionStatus.CLOSED));
    versions.put("1.1", saveVersion(main, "1.1 Reporting",
        "Spent time reporting, exports and the redesigned dashboards.", today.plusDays(24),
        VersionStatus.OPEN));
    versions.put("2.0", saveVersion(main, "2.0 Self-service",
        "Customer self-service portal and the public API.", today.plusDays(96), VersionStatus.OPEN));
    versions.put("mobile-1.0", saveVersion(mobile, "1.0 Field beta",
        "First release handed to the field team.", today.plusDays(45), VersionStatus.OPEN));
    return versions;
  }

  private VersionDto saveVersion(ProjectDto project, String name, String description,
      LocalDate effectiveDate, VersionStatus status) {
    VersionDto version = VersionDto.builder().project(project).name(name).description(description)
        .effectiveDate(effectiveDate).status(status).build();
    version.setId(versionService.save(version, false));
    return version;
  }

  private Map<String, IssueCategoryDto> seedCategories(ProjectDto main, ProjectDto mobile,
      DemoData data) {
    Map<String, IssueCategoryDto> categories = new LinkedHashMap<>();
    categories.put("backend", saveCategory(main, "Backend", data.user(USER_DEV1)));
    categories.put("frontend", saveCategory(main, "Frontend", data.user(USER_DEV2)));
    categories.put("infra", saveCategory(main, "Infrastructure", null));
    categories.put("docs", saveCategory(main, "Documentation", null));
    categories.put("mobile-app", saveCategory(mobile, "Application", data.user(USER_DEV1)));
    categories.put("mobile-api", saveCategory(mobile, "API client", null));
    return categories;
  }

  private IssueCategoryDto saveCategory(ProjectDto project, String name, UserDto owner) {
    IssueCategoryDto category =
        IssueCategoryDto.builder().project(project).name(name).user(owner).build();
    category.setId(issueCategoryService.save(category));
    return category;
  }

  // ---------------------------------------------------------------- issues

  /**
   * Describes one seeded issue. {@code startOffset} and {@code dueOffset} are days relative to
   * today, so the Gantt and the roadmap stay meaningful however long after this code was written
   * the demo is run.
   */
  private record IssueSpec(String tracker, String subject, String description, String status,
      String priority, String assignee, String category, String version, int startOffset,
      int dueOffset, int doneRatio, Double estimatedHours) {}

  private void seedMainProjectIssues(ProjectDto project, DemoData data,
      Map<String, VersionDto> versions, Map<String, IssueCategoryDto> categories,
      LocalDate today) {

    List<IssueSpec> delivered = List.of(
        spec(TRACKER_EPIC, "Billing and invoicing", "Umbrella for the whole billing effort.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_PM, null, "1.0", -120, -76, 100, 240.0),
        spec(TRACKER_TASK, "Design the account data model",
            "Entity relationship diagram and migration plan for accounts and contracts.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_PM, "backend", "1.0", -118, -110, 100, 24.0),
        spec(TRACKER_FEATURE, "Account registration and email confirmation",
            "Self-service sign-up with a double opt-in confirmation email.", STATUS_CLOSED,
            PRIORITY_HIGH, USER_DEV1, "backend", "1.0", -110, -96, 100, 40.0),
        spec(TRACKER_FEATURE, "Password reset flow",
            "Time-limited reset links, rate limited per account.", STATUS_CLOSED, PRIORITY_NORMAL,
            USER_DEV2, "backend", "1.0", -104, -94, 100, 16.0),
        spec(TRACKER_FEATURE, "Invoice generation",
            "Generate monthly invoices as PDF and archive them per account.", STATUS_CLOSED,
            PRIORITY_HIGH, USER_DEV1, "backend", "1.0", -96, -82, 100, 56.0),
        spec(TRACKER_BUG, "Invoice totals rounded inconsistently",
            "Line items rounded to two decimals individually, so the total drifted by a cent on "
                + "large invoices.",
            STATUS_CLOSED, PRIORITY_URGENT, USER_DEV1, "backend", "1.0", -88, -85, 100, 6.0),
        spec(TRACKER_TASK, "Set up the staging environment",
            "Provision staging, wire the deployment pipeline and seed anonymised data.",
            STATUS_CLOSED, PRIORITY_NORMAL, null, "infra", "1.0", -112, -100, 100, 32.0),
        spec(TRACKER_TASK, "Continuous integration for the backend",
            "Build, unit tests and static analysis on every pull request.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV2, "infra", "1.0", -108, -99, 100, 20.0),
        spec(TRACKER_FEATURE, "Role based access control",
            "Map the product's roles onto the permission checks in the service layer.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_DEV2, "backend", "1.0", -94, -80, 100, 48.0),
        spec(TRACKER_BUG, "Session dropped when switching between tabs",
            "Concurrent refresh requests invalidated each other's tokens.", STATUS_CLOSED,
            PRIORITY_HIGH, USER_DEV2, "frontend", "1.0", -86, -83, 100, 10.0),
        spec(TRACKER_TASK, "Write the administrator handbook",
            "Installation, configuration and day-to-day operations.", STATUS_CLOSED,
            PRIORITY_LOW, USER_PM, "docs", "1.0", -90, -78, 100, 24.0),
        spec(TRACKER_SUPPORT, "Northwind asks for a data export before migrating",
            "Full account and invoice export in CSV, delivered over SFTP.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV1, null, null, -84, -80, 100, 8.0),
        spec(TRACKER_BUG, "Confirmation email lands in spam for some domains",
            "No new account could complete sign-up: the SPF record was missing the transactional "
                + "mail provider.",
            STATUS_CLOSED, PRIORITY_IMMEDIATE, null, "infra", "1.0", -82, -79, 100, 5.0),
        spec(TRACKER_FEATURE, "Audit log for account changes",
            "Every change to an account is recorded with actor, timestamp and previous value.",
            STATUS_CLOSED, PRIORITY_NORMAL, USER_DEV1, "backend", "1.0", -92, -77, 100, 36.0),
        spec(TRACKER_BUG, "Invoice PDF missing the tax identifier",
            "Template referenced a field that was only populated for corporate accounts.",
            STATUS_CLOSED, PRIORITY_NORMAL, USER_DEV1, "backend", "1.0", -80, -77, 100, 4.0),
        spec(TRACKER_TASK, "Release 1.0", "Cut the release branch, tag and publish.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_PM, "infra", "1.0", -77, -75, 100, 8.0));

    List<IssueSpec> inFlight = List.of(
        spec(TRACKER_EPIC, "Reporting back office",
            "Everything the 1.1 release groups together: reports, exports and dashboards.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_PM, null, "1.1", -60, 24, 65, 180.0),
        spec(TRACKER_TASK, "Choose the reporting aggregation strategy",
            "Compare materialised views against on-the-fly aggregation for the spent time report.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_PM, "backend", "1.1", -60, -52, 100, 16.0),
        spec(TRACKER_FEATURE, "Spent time report grouped by any attribute",
            "Group logged time by project, user, activity or issue, with period aggregation.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_DEV1, "backend", "1.1", -52, -34, 100, 64.0),
        spec(TRACKER_FEATURE, "Dashboard widgets for the landing page",
            "Configurable blocks showing assigned, reported and watched issues.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV2, "frontend", "1.1", -46, -28, 100, 40.0),
        spec(TRACKER_BUG, "Report totals ignore the selected period",
            "The period filter was applied to the grid but not to the totals row.", STATUS_CLOSED,
            PRIORITY_URGENT, USER_DEV1, "backend", "1.1", -32, -29, 100, 6.0),
        spec(TRACKER_FEATURE, "Saved custom queries",
            "Let a user save a filter combination and share it with the project.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV2, "frontend", "1.1", -30, -14, 100, 32.0),
        spec(TRACKER_BUG, "Grouping by assignee loses the unassigned bucket",
            "Issues with no assignee were dropped from the grouped view instead of grouped "
                + "under a blank heading.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_DEV2, "frontend", "1.1", -26, -22, 100, 8.0),
        spec(TRACKER_TASK, "Performance pass on the issue list",
            "Paginated fetching and an index on the columns the default filter sorts by.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_DEV1, "backend", "1.1", -24, -12, 100, 28.0),
        spec(TRACKER_BUG, "Time entry list slow with three months of data",
            "Missing index on spent_on made the default range scan the whole table.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_DEV1, "backend", "1.1", -20, -16, 100, 10.0),
        spec(TRACKER_TASK, "Accessibility review of the reporting screens",
            "Keyboard navigation, focus order and contrast on the new dashboards.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_QA, "frontend", "1.1", -18, -8, 100, 20.0),
        spec(TRACKER_BUG, "Export produces an empty file for large ranges",
            "The streaming writer was closed before the last flush.", STATUS_CLOSED,
            PRIORITY_URGENT, USER_DEV1, "backend", "1.1", -14, -10, 100, 7.0),
        spec(TRACKER_SUPPORT, "Contoso cannot reconcile the March report",
            "Their finance team compared the report against invoices issued in a different "
                + "timezone.",
            STATUS_CLOSED, PRIORITY_NORMAL, USER_PM, null, null, -16, -12, 100, 4.0),
        spec(TRACKER_FEATURE, "Bulk edit from the issue list",
            "Change status, assignee or version on a selection of issues at once.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV2, "frontend", "1.1", -12, -2, 100, 24.0),
        spec(TRACKER_TASK, "Document the reporting API",
            "Endpoints, filters and pagination for the reporting service.", STATUS_CLOSED,
            PRIORITY_LOW, USER_PM, "docs", "1.1", -10, -1, 100, 12.0),
        spec(TRACKER_BUG, "Date picker offset by one day in negative timezones",
            "The picker sent a local date that the server reinterpreted as UTC.", STATUS_CLOSED,
            PRIORITY_HIGH, USER_DEV2, "frontend", "1.1", -22, -18, 100, 9.0),
        spec(TRACKER_TASK, "Migrate the dashboards to the new chart library",
            "Replace the deprecated charting dependency before it stops receiving updates.",
            STATUS_CLOSED, PRIORITY_NORMAL, USER_DEV2, "frontend", "1.1", -8, 2, 100, 18.0));

    List<IssueSpec> rejected = List.of(
        spec(TRACKER_FEATURE, "Real-time collaborative editing of issues",
            "Rejected: the cost of conflict resolution does not justify the benefit for the "
                + "expected number of concurrent editors.",
            STATUS_REJECTED, PRIORITY_LOW, null, "frontend", null, -55, -50, 0, 120.0),
        spec(TRACKER_BUG, "Dashboard flickers on Internet Explorer",
            "Rejected: the browser is out of support for this product.", STATUS_REJECTED,
            PRIORITY_LOW, null, "frontend", null, -44, -42, 0, null),
        spec(TRACKER_FEATURE, "Per-user themeable colour palette",
            "Rejected for now: revisit once the design system stabilises.", STATUS_REJECTED,
            PRIORITY_LOW, null, "frontend", "2.0", -36, -34, 0, 40.0));

    // The only issues left open. Assigned to admin so that My Page has content for the account the
    // demo hands out, and scheduled around today so they sit in the middle of the Gantt.
    List<IssueSpec> open = List.of(
        spec(TRACKER_FEATURE, "Export the spent time report to CSV",
            "Stream the currently filtered report, honouring the selected grouping and period.",
            STATUS_IN_PROGRESS, PRIORITY_HIGH, USER_ADMIN, "backend", "1.1", -6, 12, 45, 24.0),
        spec(TRACKER_BUG, "Gantt loses the dependency arrows when zooming out",
            "Precedency arrows are dropped below a certain zoom level instead of being "
                + "aggregated.",
            STATUS_IN_REVIEW, PRIORITY_HIGH, USER_ADMIN, "frontend", "1.1", -4, 8, 85, 12.0),
        spec(TRACKER_TASK, "Single sign-on rollout",
            "Blocked until the identity provider renews the signing certificate.", STATUS_BLOCKED,
            PRIORITY_URGENT, USER_ADMIN, "infra", "2.0", -2, 30, 20, 40.0));

    List<IssueSpec> all = new ArrayList<>();
    all.addAll(delivered);
    all.addAll(inFlight);
    all.addAll(rejected);
    all.addAll(open);

    List<IssueDto> created = createIssues(project, all, data, versions, categories, today);

    // Subtasks: the two epics adopt the work that belongs to them, which gives the issue list its
    // parent/child rows and the Gantt its summary bars.
    attachChildren(created, "Billing and invoicing",
        List.of("Design the account data model", "Account registration and email confirmation",
            "Invoice generation", "Audit log for account changes"));
    attachChildren(created, "Reporting back office",
        List.of("Choose the reporting aggregation strategy",
            "Spent time report grouped by any attribute", "Dashboard widgets for the landing page",
            "Saved custom queries", "Export the spent time report to CSV"));

    // Precedencies, so the Gantt shows real dependency arrows rather than free-floating bars.
    relate(created, "Design the account data model",
        "Account registration and email confirmation", IssueRelationType.PRECEDES);
    relate(created, "Account registration and email confirmation", "Invoice generation",
        IssueRelationType.PRECEDES);
    relate(created, "Choose the reporting aggregation strategy",
        "Spent time report grouped by any attribute", IssueRelationType.PRECEDES);
    relate(created, "Spent time report grouped by any attribute",
        "Export the spent time report to CSV", IssueRelationType.PRECEDES);
    relate(created, "Performance pass on the issue list",
        "Time entry list slow with three months of data", IssueRelationType.RELATES);
    relate(created, "Single sign-on rollout", "Role based access control",
        IssueRelationType.RELATES);

    seedWatchers(created, data);
    seedTimeEntries(project, created, data, today);
  }

  private void seedMobileIssues(ProjectDto project, DemoData data,
      Map<String, VersionDto> versions, Map<String, IssueCategoryDto> categories,
      LocalDate today) {
    List<IssueSpec> specs = List.of(
        spec(TRACKER_TASK, "Bootstrap the mobile project",
            "Project skeleton, dependency baseline and the release pipeline.", STATUS_CLOSED,
            PRIORITY_HIGH, USER_DEV1, "mobile-app", "mobile-1.0", -50, -44, 100, 24.0),
        spec(TRACKER_FEATURE, "Sign in against the platform",
            "Reuse the platform's authentication, including the refresh token flow.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_DEV1, "mobile-api", "mobile-1.0", -44, -32, 100,
            32.0),
        spec(TRACKER_FEATURE, "Offline cache for the assigned issue list",
            "Keep the last synchronised list readable with no connectivity.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV1, "mobile-app", "mobile-1.0", -32, -18, 100, 48.0),
        spec(TRACKER_BUG, "List scroll jumps after a background sync",
            "The adapter reset its position when the data set was replaced wholesale.",
            STATUS_CLOSED, PRIORITY_NORMAL, USER_DEV2, "mobile-app", "mobile-1.0", -20, -16, 100,
            8.0),
        spec(TRACKER_FEATURE, "Log time from the issue detail screen",
            "Quick entry with the activity remembered from the previous log.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV1, "mobile-app", "mobile-1.0", -16, -4, 100, 28.0),
        spec(TRACKER_BUG, "Push notifications duplicated on Android",
            "The device token was registered again on every cold start.", STATUS_CLOSED,
            PRIORITY_HIGH, USER_DEV1, "mobile-api", "mobile-1.0", -12, -8, 100, 10.0),
        spec(TRACKER_TASK, "Field beta test plan",
            "Scenarios, devices and the feedback channel for the field trial.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_DEV2, "mobile-app", "mobile-1.0", -10, 4, 100, 16.0),
        spec(TRACKER_SUPPORT, "Field team asks for a larger tap target on the log button",
            "Reported during the first trial day.", STATUS_CLOSED, PRIORITY_LOW, USER_DEV2,
            "mobile-app", null, -6, -3, 100, 2.0),
        spec(TRACKER_FEATURE, "Attach a photo to an issue",
            "Camera capture with client side downscaling before upload.", STATUS_REJECTED,
            PRIORITY_LOW, null, "mobile-app", null, -8, -6, 0, 24.0));

    List<IssueDto> created = createIssues(project, specs, data, versions, categories, today);
    relate(created, "Bootstrap the mobile project", "Sign in against the platform",
        IssueRelationType.PRECEDES);
    relate(created, "Sign in against the platform", "Offline cache for the assigned issue list",
        IssueRelationType.PRECEDES);
    seedTimeEntries(project, created, data, today);
  }

  private void seedLegacyIssues(ProjectDto project, DemoData data, LocalDate today) {
    List<IssueSpec> specs = List.of(
        spec(TRACKER_TASK, "Freeze the legacy portal",
            "Stop accepting changes and point the documentation at the new platform.",
            STATUS_CLOSED, PRIORITY_NORMAL, USER_PM, null, null, -150, -140, 100, 8.0),
        spec(TRACKER_TASK, "Migrate the remaining accounts",
            "Move the last accounts onto the new platform and verify their invoices.",
            STATUS_CLOSED, PRIORITY_HIGH, USER_PM, null, null, -140, -125, 100, 40.0),
        spec(TRACKER_BUG, "Statement download broken after the migration",
            "Fixed in place while the portal was still serving traffic.", STATUS_CLOSED,
            PRIORITY_HIGH, USER_PM, null, null, -132, -130, 100, 6.0),
        spec(TRACKER_TASK, "Archive the portal database",
            "Cold storage snapshot kept for the legal retention period.", STATUS_CLOSED,
            PRIORITY_NORMAL, USER_PM, null, null, -125, -120, 100, 12.0));

    createIssues(project, specs, data, Map.of(), Map.of(), today);
  }

  private static IssueSpec spec(String tracker, String subject, String description, String status,
      String priority, String assignee, String category, String version, int startOffset,
      int dueOffset, int doneRatio, Double estimatedHours) {
    return new IssueSpec(tracker, subject, description, status, priority, assignee, category,
        version, startOffset, dueOffset, doneRatio, estimatedHours);
  }

  /**
   * Creates each issue the way it would really have come into being: filed as new by its reporter,
   * then picked up and finally resolved, each step performed under that person's own identity and
   * carrying a note. That is what fills the history tab with something worth reading — attributed
   * to the right member, with the status and progress changes the appjar records by itself — and
   * it is the only way to get journal entries that are not fabricated rows.
   */
  private List<IssueDto> createIssues(ProjectDto project, List<IssueSpec> specs, DemoData data,
      Map<String, VersionDto> versions, Map<String, IssueCategoryDto> categories,
      LocalDate today) {
    List<IssueDto> created = new ArrayList<>();
    for (IssueSpec s : specs) {
      String reporter = s.assignee() == null ? USER_PM : s.assignee();
      LocalDate opened = today.plusDays(s.startOffset());
      LocalDate resolved = today.plusDays(s.dueOffset());
      boolean closed = data.status(s.status()).isClosed();

      IssueDto issue = IssueDto.builder()
          .project(project)
          .tracker(data.tracker(s.tracker()))
          .subject(s.subject())
          .description(s.description())
          // Filed as new: the target status is reached through the updates below, so the appjar
          // records the transition instead of the issue springing into existence already done.
          .status(data.status(STATUS_NEW))
          .priority(data.priority(s.priority()))
          .author(data.user(reporter))
          .category(s.category() == null ? null : categories.get(s.category()))
          .fixedVersion(s.version() == null ? null : versions.get(s.version()))
          .startDate(opened)
          .dueDate(resolved)
          .doneRatio(0)
          .estimatedHours(s.estimatedHours())
          .build();

      identity.runAs(reporter, () -> issue.setId(
          issueService.save(issue, List.of(), customFieldValues(s, data))));

      if (s.assignee() != null) {
        LocalDate startedOn = midpoint(opened, resolved);
        applyUpdate(issue, s.assignee(), startedOn, updated -> {
          updated.setAssignedUser(data.user(s.assignee()));
          updated.setStatus(data.status(STATUS_IN_PROGRESS));
          // An issue that is meant to stay in progress gets its final progress here, since the
          // step below that would otherwise set it is skipped.
          updated.setDoneRatio(STATUS_IN_PROGRESS.equals(s.status()) ? s.doneRatio()
              : Math.min(50, Math.max(10, s.doneRatio() / 2)));
        }, pickUpNote(s));
      }

      // Reaching the final status: skipped when the issue is meant to stay "In progress", which is
      // already where the previous step left it.
      if (!STATUS_IN_PROGRESS.equals(s.status())) {
        String actor = s.assignee() == null ? USER_PM : s.assignee();
        applyUpdate(issue, actor, resolved, updated -> {
          updated.setStatus(data.status(s.status()));
          updated.setDoneRatio(s.doneRatio());
        }, resolutionNote(s));
      }

      backdateIssue(issue, opened, resolved, closed);
      created.add(issue);
    }
    return created;
  }

  /**
   * Applies one step of an issue's life under {@code actor}'s identity, with a note, and moves the
   * journal entries the appjar wrote to the date the step happened on.
   */
  private void applyUpdate(IssueDto issue, String actor, LocalDate on,
      java.util.function.Consumer<IssueDto> change, String note) {
    int journalWatermark = maxJournalId();
    identity.runAs(actor, () -> {
      change.accept(issue);
      JournalDto journal = JournalDto.builder().issue(issue).notes(note)
          .createdOn(atNineAm(on)).updatedOn(atNineAm(on)).privateNotes(false).build();
      issueService.update(issue, List.of(), Map.of(), journal);
    });
    backdateJournals(issue.getId(), journalWatermark, atNineAm(on));
  }

  private int maxJournalId() {
    Integer max =
        jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM journals", Integer.class);
    return max == null ? 0 : max;
  }

  private void backdateJournals(Integer issueId, int watermark, Instant when) {
    jdbcTemplate.update(
        "UPDATE journals SET created_on = ?, updated_on = ? "
            + "WHERE journalized_id = ? AND journalized_type = 'Issue' AND id > ?",
        java.sql.Timestamp.from(when), java.sql.Timestamp.from(when), issueId, watermark);
  }

  private static LocalDate midpoint(LocalDate from, LocalDate to) {
    return from.plusDays(Math.max(1, (to.toEpochDay() - from.toEpochDay()) / 2));
  }

  private String pickUpNote(IssueSpec s) {
    return switch (s.tracker()) {
      case TRACKER_BUG -> "Reproduced on staging, taking a look.";
      case TRACKER_SUPPORT -> "Acknowledged, getting back to the customer with an update.";
      case TRACKER_EPIC -> "Breaking this down and scheduling the first items.";
      default -> "Picked this up, starting on it now.";
    };
  }

  private String resolutionNote(IssueSpec s) {
    if (STATUS_REJECTED.equals(s.status())) {
      return "Closing as rejected — see the description for the rationale.";
    }
    if (STATUS_BLOCKED.equals(s.status())) {
      return "Parked: waiting on an external dependency before this can move again.";
    }
    if (STATUS_IN_REVIEW.equals(s.status())) {
      return "Ready for review, please take a look when you get a chance.";
    }
    return switch (s.tracker()) {
      case TRACKER_BUG -> "Fixed and verified on staging.";
      case TRACKER_SUPPORT -> "Answered and confirmed with the customer.";
      default -> "Done and merged.";
    };
  }

  /**
   * Values for the custom fields that apply to this issue's tracker, so the issue form and the
   * custom field filters have real data behind them rather than empty columns.
   */
  private Map<CustomFieldDto, String> customFieldValues(IssueSpec s, DemoData data) {
    Map<CustomFieldDto, String> values = new LinkedHashMap<>();
    switch (s.tracker()) {
      case TRACKER_BUG -> {
        values.put(data.customField(CF_SEVERITY),
            switch (s.priority()) {
              case PRIORITY_IMMEDIATE, PRIORITY_URGENT -> "Critical";
              case PRIORITY_HIGH -> "Major";
              default -> "Minor";
            });
        values.put(data.customField(CF_REGRESSION), random.nextInt(4) == 0 ? "1" : "0");
        values.put(data.customField(CF_CUSTOMER), CUSTOMERS.get(random.nextInt(CUSTOMERS.size())));
      }
      case TRACKER_SUPPORT ->
          values.put(data.customField(CF_CUSTOMER), CUSTOMERS.get(random.nextInt(CUSTOMERS.size())));
      case TRACKER_FEATURE, TRACKER_TASK, TRACKER_EPIC ->
          values.put(data.customField(CF_STORY_POINTS),
              String.valueOf(STORY_POINTS.get(random.nextInt(STORY_POINTS.size()))));
      default -> { /* no custom field applies to this tracker */ }
    }
    return values;
  }

  /**
   * Rewrites an issue's timestamps so it looks like it was filed when its work started rather than
   * at application startup, and stamps {@code closed_on} for the issues that are already done.
   */
  private void backdateIssue(IssueDto issue, LocalDate startDate, LocalDate dueDate,
      boolean closed) {
    Instant createdOn = atNineAm(startDate);
    Instant updatedOn = atNineAm(closed ? dueDate : LocalDate.now());
    jdbcTemplate.update(
        "UPDATE issues SET created_on = ?, updated_on = ?, closed_on = ? WHERE id = ?",
        java.sql.Timestamp.from(createdOn), java.sql.Timestamp.from(updatedOn),
        closed ? java.sql.Timestamp.from(atNineAm(dueDate)) : null, issue.getId());
  }

  /** Grouping issues under an epic is planning work, so it is recorded as the project manager. */
  private void attachChildren(List<IssueDto> issues, String parentSubject,
      List<String> childSubjects) {
    IssueDto parent = bySubject(issues, parentSubject);
    identity.runAs(USER_PM, () -> {
      for (String childSubject : childSubjects) {
        IssueDto child = bySubject(issues, childSubject);
        child.setParent(parent);
        issueService.update(child);
      }
    });
  }

  private void relate(List<IssueDto> issues, String fromSubject, String toSubject,
      IssueRelationType type) {
    issueRelationService.save(IssueRelationDto.builder().issueFrom(bySubject(issues, fromSubject))
        .issueTo(bySubject(issues, toSubject)).type(type).delay(type == IssueRelationType.PRECEDES
            ? 0
            : null)
        .build());
  }

  private static IssueDto bySubject(List<IssueDto> issues, String subject) {
    return issues.stream().filter(i -> subject.equals(i.getSubject())).findFirst()
        .orElseThrow(() -> new IllegalStateException("No seeded issue named '" + subject + "'"));
  }

  /** A few watchers, so the watchers panel and the "issues I watch" dashboard block are not empty. */
  private void seedWatchers(List<IssueDto> issues, DemoData data) {
    watch(issues, "Export the spent time report to CSV", data.user(USER_QA));
    watch(issues, "Export the spent time report to CSV", data.user(USER_PM));
    watch(issues, "Gantt loses the dependency arrows when zooming out", data.user(USER_ADMIN));
    watch(issues, "Single sign-on rollout", data.user(USER_PM));
    watch(issues, "Single sign-on rollout", data.user(USER_DEV2));
  }

  private void watch(List<IssueDto> issues, String subject, UserDto user) {
    watcherService.save(
        WatcherDto.builder().issue(bySubject(issues, subject)).user(user).watchableType("Issue")
            .build());
  }

  // ---------------------------------------------------------------- time entries

  /**
   * Logs time against the issues that have an estimate, spreading each issue's effort over the
   * working days between its start and due dates and across the people who could plausibly have
   * done the work. The result is a spent time report that has something to show whichever way it
   * is grouped — by project, user, activity, issue or period.
   */
  private void seedTimeEntries(ProjectDto project, List<IssueDto> issues, DemoData data,
      LocalDate today) {
    List<String> activityPool = List.of(ACTIVITY_DEVELOPMENT, ACTIVITY_DEVELOPMENT,
        ACTIVITY_DEVELOPMENT, ACTIVITY_CODE_REVIEW, ACTIVITY_QA, ACTIVITY_DESIGN,
        ACTIVITY_DOCUMENTATION, ACTIVITY_MEETING, ACTIVITY_SUPPORT);
    List<String> userPool =
        List.of(USER_DEV1, USER_DEV2, USER_QA, USER_PM, USER_DEV1, USER_DEV2, USER_ADMIN);

    for (IssueDto issue : issues) {
      if (issue.getEstimatedHours() == null || issue.getStartDate() == null) {
        continue;
      }
      // Only log time that has actually elapsed: nothing in the future, and never more entries
      // than there are days available.
      LocalDate from = issue.getStartDate();
      LocalDate to = issue.getDueDate().isAfter(today) ? today : issue.getDueDate();
      if (from.isAfter(to)) {
        continue;
      }

      int entries = Math.min(6, Math.max(1, (int) Math.round(issue.getEstimatedHours() / 8)));
      for (int i = 0; i < entries; i++) {
        LocalDate spentOn = workingDayBetween(from, to);
        String login = issue.getAssignedUser() instanceof UserDto assignee && random.nextBoolean()
            ? assignee.getLogin()
            : userPool.get(random.nextInt(userPool.size()));
        double hours = Math.round((1.5 + random.nextDouble() * 5) * 4) / 4.0;

        TimeEntryDto entry = TimeEntryDto.builder()
            .project(project)
            .issue(issue)
            .user(data.user(login))
            .author(data.user(login))
            .activity(data.activity(activityPool.get(random.nextInt(activityPool.size()))))
            .hours(hours)
            .comments(issue.getTracker().getName() + " work on " + issue.getSubject())
            .spentOn(spentOn)
            .tyear(spentOn.getYear())
            .tweek(spentOn.get(WeekFields.ISO.weekOfWeekBasedYear()))
            .build();
        entry.setId(timeEntryService.save(entry));
        backdateTimeEntry(entry, spentOn);
      }
    }
  }

  /**
   * Moves a time entry's creation timestamp to the evening of the day the time was spent on.
   *
   * <p>Besides making the data look real, this is what keeps seeding possible at all: the free
   * licence allows ten time entries created per day, counted on {@code created_on}, so leaving
   * every row stamped with the current instant would abort the seeding on the eleventh entry.
   */
  private void backdateTimeEntry(TimeEntryDto entry, LocalDate spentOn) {
    java.sql.Timestamp loggedAt =
        java.sql.Timestamp.from(spentOn.atTime(LocalTime.of(18, 0)).toInstant(ZoneOffset.UTC));
    jdbcTemplate.update(
        "UPDATE time_entries SET created_on = ?, updated_on = ? WHERE id = ?", loggedAt, loggedAt,
        entry.getId());
  }

  private LocalDate workingDayBetween(LocalDate from, LocalDate to) {
    int span = (int) (to.toEpochDay() - from.toEpochDay());
    LocalDate day = from.plusDays(span <= 0 ? 0 : random.nextInt(span + 1));
    return switch (day.getDayOfWeek()) {
      case SATURDAY -> day.minusDays(1);
      case SUNDAY -> day.minusDays(2);
      default -> day;
    };
  }

  private static Instant atNineAm(LocalDate date) {
    return date.atTime(LocalTime.of(9, 0)).toInstant(ZoneOffset.UTC);
  }
}

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
import static com.appjars.issuetracker.demo.data.DemoData.Names.ROLE_ADMINISTRATOR;
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

import com.appjars.issuetracker.business.service.CustomFieldService;
import com.appjars.issuetracker.business.service.EnumerationService;
import com.appjars.issuetracker.business.service.IssueStatusService;
import com.appjars.issuetracker.business.service.RoleService;
import com.appjars.issuetracker.business.service.TrackerService;
import com.appjars.issuetracker.business.service.UserService;
import com.appjars.issuetracker.business.service.WorkflowTransitionService;
import com.appjars.issuetracker.model.CustomFieldDto;
import com.appjars.issuetracker.model.EmailDto;
import com.appjars.issuetracker.model.EnumerationDto;
import com.appjars.issuetracker.model.EnumerationType;
import com.appjars.issuetracker.model.IssueStatusDto;
import com.appjars.issuetracker.model.RoleDto;
import com.appjars.issuetracker.model.TrackerDto;
import com.appjars.issuetracker.model.UserDto;
import com.appjars.issuetracker.model.UserPreferencesDto;
import com.appjars.issuetracker.model.WorkflowTransitionDto;
import com.appjars.issuetracker.model.enums.CustomFieldFormat;
import com.appjars.issuetracker.model.enums.CustomFieldType;
import com.appjars.issuetracker.model.enums.EmailNotificationType;
import com.appjars.issuetracker.model.enums.IssueVisibility;
import com.appjars.issuetracker.model.enums.RolePermission;
import com.appjars.issuetracker.model.enums.TimeEntryVisibility;
import com.appjars.issuetracker.model.enums.UserStatus;
import com.appjars.issuetracker.model.enums.UserVisibility;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Seeds everything that is not project content: priorities and activities, issue statuses,
 * trackers, roles, users, custom fields and the workflow matrix that ties statuses, trackers and
 * roles together.
 *
 * <p>Runs before {@link DemoProjectSeeder} and returns the {@link DemoData} it produced so the
 * project content can refer to these entries.
 */
@Component
@RequiredArgsConstructor
class DemoCatalogSeeder {

  /**
   * Password for every seeded user other than {@code admin}. Documented in the demo's README: the
   * whole point of having four accounts is that a visitor can sign in as each role and see the
   * application offer different things. (The landing page still only advertises {@code admin};
   * surfacing the rest belongs with the landing page rework.)
   */
  static final String DEMO_PASSWORD = "demo";

  /** Permissions that are granted per tracker rather than globally (see {@link RoleDto}). */
  private static final Set<RolePermission> TRACKER_SCOPED_PERMISSIONS =
      EnumSet.of(RolePermission.VIEW_ISSUES, RolePermission.ADD_ISSUES, RolePermission.EDIT_ISSUES,
          RolePermission.ADD_ISSUE_NOTES, RolePermission.DELETE_ISSUES);

  private final EnumerationService enumerationService;
  private final IssueStatusService issueStatusService;
  private final TrackerService trackerService;
  private final RoleService roleService;
  private final CustomFieldService customFieldService;
  private final WorkflowTransitionService workflowTransitionService;

  private final UserService userService;

  DemoData seed() {
    DemoData data = new DemoData();
    seedPriorities(data);
    seedActivities(data);
    seedIssueStatuses(data);
    seedTrackers(data);
    seedRoles(data);
    seedUsers(data);
    seedCustomFields(data);
    seedWorkflow(data);
    return data;
  }

  // ---------------------------------------------------------------- enumerations

  /**
   * Issue priorities. Redmine derives the CSS class of a priority from its distance to the default
   * one rather than from its name, and the appjar reproduces that in EnumerationListView; the
   * position names below are what that algorithm produces for five priorities with the second one
   * as the default, so a priority added later through the UI lands on a consistent scale.
   */
  private void seedPriorities(DemoData data) {
    data.priorities.put(PRIORITY_LOW, priority(PRIORITY_LOW, 1, "lowest", false));
    data.priorities.put(PRIORITY_NORMAL, priority(PRIORITY_NORMAL, 2, "default", true));
    data.priorities.put(PRIORITY_HIGH, priority(PRIORITY_HIGH, 3, "high3", false));
    data.priorities.put(PRIORITY_URGENT, priority(PRIORITY_URGENT, 4, "high2", false));
    data.priorities.put(PRIORITY_IMMEDIATE, priority(PRIORITY_IMMEDIATE, 5, "highest", false));
  }

  private void seedActivities(DemoData data) {
    data.activities.put(ACTIVITY_DESIGN, activity(ACTIVITY_DESIGN, 1, false));
    data.activities.put(ACTIVITY_DEVELOPMENT, activity(ACTIVITY_DEVELOPMENT, 2, true));
    data.activities.put(ACTIVITY_CODE_REVIEW, activity(ACTIVITY_CODE_REVIEW, 3, false));
    data.activities.put(ACTIVITY_QA, activity(ACTIVITY_QA, 4, false));
    data.activities.put(ACTIVITY_DOCUMENTATION, activity(ACTIVITY_DOCUMENTATION, 5, false));
    data.activities.put(ACTIVITY_MEETING, activity(ACTIVITY_MEETING, 6, false));
    data.activities.put(ACTIVITY_SUPPORT, activity(ACTIVITY_SUPPORT, 7, false));
  }

  private EnumerationDto priority(String name, int position, String positionName, boolean byDefault) {
    return saveEnumeration(EnumerationDto.builder().name(name).position(position)
        .positionName(positionName).defaultValue(byDefault).active(true)
        .type(EnumerationType.ISSUE_PRIORITY).build());
  }

  private EnumerationDto activity(String name, int position, boolean byDefault) {
    return saveEnumeration(EnumerationDto.builder().name(name).position(position)
        .defaultValue(byDefault).active(true).type(EnumerationType.TIME_ENTRY_ACTIVITY).build());
  }

  private EnumerationDto saveEnumeration(EnumerationDto enumeration) {
    enumeration.setId(enumerationService.save(enumeration));
    return enumeration;
  }

  // ---------------------------------------------------------------- statuses and trackers

  /**
   * Issue statuses, ordered as the lifecycle they describe. Only the first four are open; see
   * {@link DemoProjectSeeder} for why the seeded backlog keeps very few issues in them.
   */
  private void seedIssueStatuses(DemoData data) {
    data.statuses.put(STATUS_NEW, status(STATUS_NEW, 1, false, 0));
    data.statuses.put(STATUS_IN_PROGRESS, status(STATUS_IN_PROGRESS, 2, false, 30));
    data.statuses.put(STATUS_IN_REVIEW, status(STATUS_IN_REVIEW, 3, false, 80));
    data.statuses.put(STATUS_BLOCKED, status(STATUS_BLOCKED, 4, false, null));
    data.statuses.put(STATUS_CLOSED, status(STATUS_CLOSED, 5, true, 100));
    data.statuses.put(STATUS_REJECTED, status(STATUS_REJECTED, 6, true, null));
  }

  private IssueStatusDto status(String name, int position, boolean closed, Integer doneRatio) {
    IssueStatusDto status = IssueStatusDto.builder().name(name).position(position).isClosed(closed)
        .defaultDoneRatio(doneRatio).description("").build();
    status.setId(issueStatusService.save(status));
    return status;
  }

  /**
   * Trackers, each enabling a different set of standard fields so the tracker administration
   * screen shows real variation rather than five identical rows. Support requests carry no
   * scheduling fields, and epics are the only tracker that is not shown on the roadmap.
   */
  private void seedTrackers(DemoData data) {
    IssueStatusDto initial = data.status(STATUS_NEW);

    data.trackers.put(TRACKER_BUG, saveTracker(TrackerDto.builder().name(TRACKER_BUG).position(1)
        .description("Something is broken and has to be fixed.").isInRoadmap(true)
        .issueStatusDto(initial).parentTaskField(false).estimatedTimeField(false).build()));

    data.trackers.put(TRACKER_FEATURE,
        saveTracker(TrackerDto.builder().name(TRACKER_FEATURE).position(2)
            .description("New functionality requested by a stakeholder.").isInRoadmap(true)
            .issueStatusDto(initial).build()));

    data.trackers.put(TRACKER_TASK, saveTracker(TrackerDto.builder().name(TRACKER_TASK).position(3)
        .description("Planned work that is neither a defect nor a new feature.").isInRoadmap(true)
        .issueStatusDto(initial).build()));

    data.trackers.put(TRACKER_SUPPORT,
        saveTracker(TrackerDto.builder().name(TRACKER_SUPPORT).position(4)
            .description("Question or assistance request coming from a customer.")
            .isInRoadmap(false).issueStatusDto(initial).startDateField(false).dueDateField(false)
            .targetVersionField(false).estimatedTimeField(false).percentageDoneField(false)
            .parentTaskField(false).build()));

    data.trackers.put(TRACKER_EPIC, saveTracker(TrackerDto.builder().name(TRACKER_EPIC).position(5)
        .description("Long running effort grouping several issues.").isInRoadmap(false)
        .issueStatusDto(initial).categoryField(false).build()));
  }

  private TrackerDto saveTracker(TrackerDto tracker) {
    tracker.setId(trackerService.save(tracker));
    return tracker;
  }

  // ---------------------------------------------------------------- roles

  /**
   * Roles with genuinely different permission sets — the point of the demo is that switching user
   * visibly changes what the application offers, so a Reporter really cannot create issues and a
   * Developer really cannot manage members or versions.
   *
   * <p>Administrator already exists: it is part of the appjar's bootstrap data, and the
   * {@code admin} account is a system administrator anyway, which bypasses role checks.
   */
  private void seedRoles(DemoData data) {
    roleService.findByName(ROLE_ADMINISTRATOR)
        .ifPresent(role -> data.roles.put(ROLE_ADMINISTRATOR, role));

    data.roles.put(ROLE_PROJECT_MANAGER, saveRole(ROLE_PROJECT_MANAGER, 2, EnumSet.of(
        RolePermission.EDIT_PROJECT, RolePermission.CLOSE_PROJECT,
        RolePermission.SELECT_PROJECT_MODULES, RolePermission.MANAGE_MEMBERS,
        RolePermission.MANAGE_VERSIONS, RolePermission.ADD_SUBPROJECTS,
        RolePermission.MANAGE_CATEGORIES, RolePermission.MANAGE_PUBLIC_QUERIES,
        RolePermission.SAVE_QUERIES, RolePermission.VIEW_ISSUES, RolePermission.VIEW_GANTT,
        RolePermission.ADD_ISSUES, RolePermission.EDIT_ISSUES, RolePermission.DELETE_ISSUES,
        RolePermission.COPY_ISSUES, RolePermission.MANAGE_ISSUE_RELATIONS,
        RolePermission.MANAGE_SUBTASKS, RolePermission.ADD_ISSUE_NOTES,
        RolePermission.EDIT_ISSUE_NOTES, RolePermission.VIEW_PRIVATE_NOTES,
        RolePermission.SET_ISSUES_PRIVATE, RolePermission.VIEW_ISSUE_WATCHERS,
        RolePermission.ADD_ISSUE_WATCHERS, RolePermission.DELETE_ISSUE_WATCHERS,
        RolePermission.VIEW_TIME_ENTRIES, RolePermission.LOG_TIME,
        RolePermission.EDIT_TIME_ENTRIES, RolePermission.LOG_TIME_FOR_OTHER_USERS,
        RolePermission.MANAGE_PROJECT_ACTIVITIES),
        IssueVisibility.ALL, TimeEntryVisibility.ALL));

    data.roles.put(ROLE_DEVELOPER, saveRole(ROLE_DEVELOPER, 3, EnumSet.of(
        RolePermission.SAVE_QUERIES, RolePermission.VIEW_ISSUES, RolePermission.VIEW_GANTT,
        RolePermission.ADD_ISSUES, RolePermission.EDIT_ISSUES, RolePermission.COPY_ISSUES,
        RolePermission.MANAGE_ISSUE_RELATIONS, RolePermission.MANAGE_SUBTASKS,
        RolePermission.ADD_ISSUE_NOTES, RolePermission.EDIT_OWN_ISSUE_NOTES,
        RolePermission.VIEW_ISSUE_WATCHERS, RolePermission.ADD_ISSUE_WATCHERS,
        RolePermission.VIEW_TIME_ENTRIES, RolePermission.LOG_TIME,
        RolePermission.EDIT_OWN_TIME_ENTRIES),
        IssueVisibility.ALL, TimeEntryVisibility.ALL));

    data.roles.put(ROLE_QA, saveRole(ROLE_QA, 4, EnumSet.of(
        RolePermission.SAVE_QUERIES, RolePermission.VIEW_ISSUES, RolePermission.VIEW_GANTT,
        RolePermission.ADD_ISSUES, RolePermission.EDIT_ISSUES, RolePermission.ADD_ISSUE_NOTES,
        RolePermission.EDIT_OWN_ISSUE_NOTES, RolePermission.VIEW_ISSUE_WATCHERS,
        RolePermission.ADD_ISSUE_WATCHERS, RolePermission.VIEW_TIME_ENTRIES,
        RolePermission.LOG_TIME, RolePermission.EDIT_OWN_TIME_ENTRIES),
        IssueVisibility.ALL, TimeEntryVisibility.ALL));

    // Read-only on purpose: no ADD_ISSUES, no LOG_TIME, and it only sees its own issues and time.
    data.roles.put(ROLE_REPORTER, saveRole(ROLE_REPORTER, 5, EnumSet.of(
        RolePermission.VIEW_ISSUES, RolePermission.VIEW_GANTT, RolePermission.ADD_ISSUE_NOTES,
        RolePermission.VIEW_TIME_ENTRIES),
        IssueVisibility.DEFAULT, TimeEntryVisibility.OWN));
  }

  private RoleDto saveRole(String name, int position, Set<RolePermission> permissions,
      IssueVisibility issuesVisibility, TimeEntryVisibility timeEntryVisibility) {
    Map<RolePermission, List<Integer>> perTracker = new HashMap<>();
    TRACKER_SCOPED_PERMISSIONS.forEach(permission -> perTracker.put(permission, new ArrayList<>()));

    RoleDto role = RoleDto.builder().name(name).position(position).assignable(true).builtin(0)
        .permissions(new HashSet<>(permissions))
        // Empty per-tracker id lists plus the permission listed here means "applies to every
        // tracker", which is what the administration UI writes for a role with no tracker
        // restriction. Only the permissions the role actually has are granted for all trackers.
        .permissionsAllTrackers(TRACKER_SCOPED_PERMISSIONS.stream().filter(permissions::contains)
            .collect(Collectors.toCollection(HashSet::new)))
        .permissionsTrackersIds(perTracker)
        .issuesVisibility(issuesVisibility)
        .usersVisibility(UserVisibility.ALL)
        .timeEntryVisibility(timeEntryVisibility)
        .allRolesManaged(true)
        .rolesManaged(new HashSet<>())
        .build();
    role.setId(roleService.save(role));
    return role;
  }

  // ---------------------------------------------------------------- users

  /**
   * The four non-admin accounts. Five users in total is the ceiling the free licence allows, and
   * going over it also blocks editing existing users, so this list must not grow.
   */
  private void seedUsers(DemoData data) {
    userService.findByUsername(USER_ADMIN).ifPresent(user -> data.users.put(USER_ADMIN, user));

    data.users.put(USER_PM, saveUser(USER_PM, "María", "Rivas"));
    data.users.put(USER_DEV1, saveUser(USER_DEV1, "Diego", "Santos"));
    data.users.put(USER_DEV2, saveUser(USER_DEV2, "Lin", "Chen"));
    data.users.put(USER_QA, saveUser(USER_QA, "Petra", "Novak"));
  }

  private UserDto saveUser(String login, String firstName, String lastName) {
    UserDto user = UserDto.builder()
        .login(login)
        // UserService.save() generates the salt and hashes whatever it finds here, so this is the
        // plain password, not a digest.
        .hashedPassword(DEMO_PASSWORD)
        .firstName(firstName)
        .lastName(lastName)
        .admin(false)
        .status(UserStatus.ACTIVE)
        .language("en")
        .emailNotification(EmailNotificationType.ONLY_MY_EVENTS)
        .mustChangePasswd(false)
        .emails(new HashSet<>())
        // Not optional in practice: several views read the preferences without a null check (the
        // project overview reads the bookmarked projects straight off them), so a user created
        // without preferences makes those views fail as soon as that account signs in.
        // Auto-watching is left off so the only watchers in the demo are the ones seeded on
        // purpose, rather than every issue its author happened to touch.
        .preferences(UserPreferencesDto.builder()
            .watchIssuesCreated(false)
            .watchIssuesContributedTo(false)
            .noSelfNotified(true)
            .bookmarkedProjects(new ArrayList<>())
            .recentlyUsedProjects(new ArrayList<>())
            .myPageBlocks(new ArrayList<>())
            .timeZone("")
            .build())
        .build();
    user.getEmails().add(EmailDto.builder().address(login + "@example.com").defaultAddress(true)
        .notify(true).user(user).build());
    user.setId(userService.save(user));
    return user;
  }

  // ---------------------------------------------------------------- custom fields

  /**
   * One custom field per format the issue form renders differently, so the custom field
   * administration screen and the issue form both have something to show. They apply to every
   * project ({@code forAll}) but not to every tracker: severity and regression only make sense on
   * a bug, story points only on planned work.
   */
  private void seedCustomFields(DemoData data) {
    data.customFields.put(CF_SEVERITY, saveCustomField(CustomFieldDto.builder()
        .name(CF_SEVERITY).format(CustomFieldFormat.LIST).type(CustomFieldType.ISSUE).position(1)
        .description("How badly the defect affects the product.")
        .possibleValues("---\n- Minor\n- Major\n- Critical")
        .defaultValue("Major").required(false).filter(true).searchable(false).forAll(true)
        .trackers(new HashSet<>(Set.of(data.tracker(TRACKER_BUG))))
        .build()));

    data.customFields.put(CF_REGRESSION, saveCustomField(CustomFieldDto.builder()
        .name(CF_REGRESSION).format(CustomFieldFormat.BOOLEAN).type(CustomFieldType.ISSUE)
        .position(2).description("Whether the defect used to work in an earlier release.")
        .required(false).filter(true).searchable(false).forAll(true)
        .trackers(new HashSet<>(Set.of(data.tracker(TRACKER_BUG))))
        .build()));

    data.customFields.put(CF_STORY_POINTS, saveCustomField(CustomFieldDto.builder()
        .name(CF_STORY_POINTS).format(CustomFieldFormat.INTEGER).type(CustomFieldType.ISSUE)
        .position(3).description("Relative estimate used for planning.")
        .required(false).filter(true).searchable(false).forAll(true)
        .trackers(new HashSet<>(
            Set.of(data.tracker(TRACKER_FEATURE), data.tracker(TRACKER_TASK),
                data.tracker(TRACKER_EPIC))))
        .build()));

    data.customFields.put(CF_CUSTOMER, saveCustomField(CustomFieldDto.builder()
        .name(CF_CUSTOMER).format(CustomFieldFormat.TEXT).type(CustomFieldType.ISSUE).position(4)
        .description("Account that reported the request.")
        .required(false).filter(true).searchable(true).forAll(true).maxLength(60)
        .trackers(new HashSet<>(Set.of(data.tracker(TRACKER_SUPPORT), data.tracker(TRACKER_BUG))))
        .build()));
  }

  private CustomFieldDto saveCustomField(CustomFieldDto customField) {
    customField.setId(customFieldService.save(customField));
    return customField;
  }

  // ---------------------------------------------------------------- workflow

  /**
   * The workflow matrix: which status transitions each role may perform on each tracker.
   *
   * <p>The three roles that work on issues get progressively narrower rights, which is what makes
   * the workflow administration screen worth looking at: a project manager may move an issue
   * anywhere, a developer may push work forward and close it but not reject it, and QA may only
   * send work back or close it once reviewed. Reporter gets no transitions at all — it is a
   * read-only role.
   */
  private void seedWorkflow(DemoData data) {
    IssueStatusDto brandNew = data.status(STATUS_NEW);
    IssueStatusDto inProgress = data.status(STATUS_IN_PROGRESS);
    IssueStatusDto inReview = data.status(STATUS_IN_REVIEW);
    IssueStatusDto blocked = data.status(STATUS_BLOCKED);
    IssueStatusDto closed = data.status(STATUS_CLOSED);
    IssueStatusDto rejected = data.status(STATUS_REJECTED);

    List<IssueStatusDto> all =
        List.of(brandNew, inProgress, inReview, blocked, closed, rejected);

    // Project managers may go from anything to anything else.
    for (TrackerDto tracker : data.trackers.values()) {
      for (IssueStatusDto from : all) {
        for (IssueStatusDto to : all) {
          if (!from.getName().equals(to.getName())) {
            saveTransition(tracker, data.role(ROLE_PROJECT_MANAGER), from, to);
          }
        }
      }
    }

    for (TrackerDto tracker : data.trackers.values()) {
      RoleDto developer = data.role(ROLE_DEVELOPER);
      saveTransition(tracker, developer, brandNew, inProgress);
      saveTransition(tracker, developer, inProgress, inReview);
      saveTransition(tracker, developer, inProgress, blocked);
      saveTransition(tracker, developer, blocked, inProgress);
      saveTransition(tracker, developer, inReview, inProgress);
      saveTransition(tracker, developer, inReview, closed);

      RoleDto qa = data.role(ROLE_QA);
      saveTransition(tracker, qa, inReview, closed);
      saveTransition(tracker, qa, inReview, inProgress);
      saveTransition(tracker, qa, closed, inProgress);
      saveTransition(tracker, qa, brandNew, rejected);
    }
  }

  private void saveTransition(TrackerDto tracker, RoleDto role, IssueStatusDto from,
      IssueStatusDto to) {
    workflowTransitionService.save(WorkflowTransitionDto.builder().tracker(tracker).role(role)
        .oldStatus(from).newStatus(to).author(false).assignee(false).build());
  }
}

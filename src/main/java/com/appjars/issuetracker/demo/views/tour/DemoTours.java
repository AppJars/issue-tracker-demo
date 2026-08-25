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
package com.appjars.issuetracker.demo.views.tour;

import com.appjars.issuetracker.flow.component.IssueListComponent;
import com.appjars.issuetracker.flow.component.SpentTimeReportComponent;
import com.appjars.issuetracker.flow.view.GanttView;
import com.appjars.issuetracker.flow.view.ProjectListView;
import com.appjars.issuetracker.flow.view.RoadmapView;
import com.appjars.issuetracker.flow.view.TimeEntryListView;
import com.appjars.issuetracker.flow.view.WorkflowView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import java.util.ArrayList;
import java.util.List;
import org.vaadin.addons.antlerflow.tour.EngineType;
import org.vaadin.addons.antlerflow.tour.Tour;
import org.vaadin.addons.antlerflow.tour.TourButton;
import org.vaadin.addons.antlerflow.tour.TourButtonType;
import org.vaadin.addons.antlerflow.tour.TourStep;

/**
 * The guided tours offered by the demo's landing page.
 *
 * <p>Every tour is <b>read-only and lives on a single view</b>. It points at data the demo already
 * ships with and explains it; it never asks the visitor to create a project or fill in a form, and
 * it never navigates while it is running. An anonymous visitor is therefore never bounced to a
 * login form mid-tour, and the free licence limits on creating issues never come into play.
 *
 * <p>Steps anchor to the {@code id}s the appjar exposes on its components. A step whose
 * {@code attachTo} is null renders centred, which is what introductions and closing steps want.
 *
 * @see com.appjars.issuetracker.demo.views.MainLayout#startPendingTour() for how a tour selected on
 *      the landing page is started once its view has been reached
 */
public final class DemoTours {

  /** Session attribute holding the tour to start once its view has been navigated to. */
  public static final String PENDING_TOUR_ATTRIBUTE = DemoTours.class.getName() + ".pendingTour";

  static final String KEY_PREFIX = "appjars.issuetracker.demo.tour.";

  /** Prefix of the labels the landing page and the navbar use to name each tour. */
  static final String MENU_KEY_PREFIX = "appjars.issuetracker.demo.home.tour.";

  public enum DemoTour {
    NAVIGATION, PROJECTS, ISSUES, ROADMAP, GANTT, TIME_TRACKING, TIME_REPORT, WORKFLOW
  }

  private DemoTours() {}

  /**
   * The view a tour describes, or null when it describes the layout itself and therefore runs
   * wherever the visitor already is.
   */
  public static Class<? extends Component> view(DemoTour tour) {
    return switch (tour) {
      case NAVIGATION -> null;
      case PROJECTS -> ProjectListView.class;
      case ISSUES -> IssueListComponent.class;
      case ROADMAP -> RoadmapView.class;
      case GANTT -> GanttView.class;
      case TIME_TRACKING -> TimeEntryListView.class;
      case TIME_REPORT -> SpentTimeReportComponent.class;
      case WORKFLOW -> WorkflowView.class;
    };
  }

  /**
   * Whether the tour's view only exists inside a project and so needs one named in the URL.
   *
   * <p>These views render inside the appjar's project layout, which is also why whoever decides
   * that a tour's view is on screen has to look at the whole navigation chain rather than at the
   * layout's immediate content: for a project-scoped route that content is the project's tab
   * layout, not the view itself.
   */
  public static boolean isProjectScoped(DemoTour tour) {
    return tour == DemoTour.ISSUES || tour == DemoTour.ROADMAP;
  }

  /** Whether the tour describes an administration screen and so is only offered to admins. */
  public static boolean requiresAdmin(DemoTour tour) {
    return tour == DemoTour.WORKFLOW;
  }

  /**
   * The icon-and-label content of the tour's entry in a tour menu, so the landing page and the
   * navbar offer the same tours under the same names.
   */
  public static Component menuLabel(DemoTour tour,
      SerializableFunction<String, String> translator) {
    Div label = new Div(menuIcon(tour), new Span(translator.apply(MENU_KEY_PREFIX + menuKey(tour))));
    label.addClassName("demo-tour-item");
    return label;
  }

  /** The suffix under {@link #MENU_KEY_PREFIX} that names the tour in the tour menus. */
  private static String menuKey(DemoTour tour) {
    return switch (tour) {
      case NAVIGATION -> "navigation";
      case PROJECTS -> "projects";
      case ISSUES -> "issues";
      case ROADMAP -> "roadmap";
      case GANTT -> "gantt";
      case TIME_TRACKING -> "timetracking";
      case TIME_REPORT -> "timereport";
      case WORKFLOW -> "workflow";
    };
  }

  /** The icon shown next to the tour in the tour menus. */
  private static Icon menuIcon(DemoTour tour) {
    return switch (tour) {
      case NAVIGATION -> VaadinIcon.MENU.create();
      case PROJECTS -> VaadinIcon.FOLDER_OPEN.create();
      case ISSUES -> VaadinIcon.TASKS.create();
      case ROADMAP -> VaadinIcon.FLAG.create();
      case GANTT -> VaadinIcon.CHART_TIMELINE.create();
      case TIME_TRACKING -> VaadinIcon.CLOCK.create();
      case TIME_REPORT -> VaadinIcon.CHART_GRID.create();
      case WORKFLOW -> VaadinIcon.SITEMAP.create();
    };
  }

  /**
   * Cancels the tour running on {@code host}, if any. Called on every navigation, so a tour
   * abandoned by navigating away takes its overlay with it instead of highlighting an element that
   * is no longer on screen.
   */
  public static void stopRunningTour(Component host) {
    Tour running = ComponentUtil.getData(host, Tour.class);
    if (running != null) {
      running.cancel();
    }
  }

  /** Creates, attaches and starts {@code tour} on {@code host}, detaching it when it ends. */
  public static void start(DemoTour tour, Component host,
      SerializableFunction<String, String> translator, boolean isAdmin) {
    if (!host.isAttached()) {
      // The host is only attached to the UI at the end of the round trip that created it, and a
      // tour cannot start on a detached host.
      Registration[] registration = new Registration[1];
      registration[0] = host.addAttachListener(e -> {
        registration[0].remove();
        doStart(tour, host, translator, isAdmin);
      });
      return;
    }
    doStart(tour, host, translator, isAdmin);
  }

  private static void doStart(DemoTour tour, Component host,
      SerializableFunction<String, String> translator, boolean isAdmin) {
    stopRunningTour(host);
    Tour t = Tour.builder().engineType(EngineType.DRIVER).steps(steps(tour, translator, isAdmin))
        .showCancelButton(true).allowClose(true).build();
    host.getElement().appendChild(t.getElement());
    t.addTourCompletedListener(e -> cleanUp(host, t));
    t.addTourCanceledListener(e -> cleanUp(host, t));
    ComponentUtil.setData(host, Tour.class, t);
    t.start();
  }

  private static void cleanUp(Component host, Tour tour) {
    if (ComponentUtil.getData(host, Tour.class) == tour) {
      ComponentUtil.setData(host, Tour.class, null);
    }
    tour.getElement().removeFromParent();
  }

  private static List<TourStep> steps(DemoTour tour,
      SerializableFunction<String, String> t, boolean isAdmin) {
    return switch (tour) {
      case NAVIGATION -> navigationSteps(t, isAdmin);
      case PROJECTS -> projectsSteps(t);
      case ISSUES -> issuesSteps(t);
      case ROADMAP -> roadmapSteps(t);
      case GANTT -> ganttSteps(t);
      case TIME_TRACKING -> timeTrackingSteps(t);
      case TIME_REPORT -> timeReportSteps(t);
      case WORKFLOW -> workflowSteps(t);
    };
  }

  // ---------------------------------------------------------------- the tours

  /**
   * Walks the drawer. The Administration section only exists for an administrator, so its step is
   * left out entirely for everyone else rather than rendering with no target.
   */
  private static List<TourStep> navigationSteps(SerializableFunction<String, String> t,
      boolean isAdmin) {
    List<StepSpec> steps = new ArrayList<>(List.of(
        step("nav.intro", null),
        step("nav.mypage", "#drawer-mypage"),
        step("nav.projects", "#drawer-projects"),
        step("nav.issues", "#drawer-issues"),
        step("nav.gantt", "#drawer-gantt"),
        step("nav.spenttime", "#drawer-spenttime")));
    if (isAdmin) {
      steps.add(step("nav.admin", "#drawer-admin"));
    }
    return finish(steps, t, "nav.finish");
  }

  private static List<TourStep> projectsSteps(SerializableFunction<String, String> t) {
    return finish(new ArrayList<>(List.of(
        step("projects.grid", "#projects-grid"),
        step("projects.tree", "#projects-grid"),
        step("projects.status", "#projects-grid"),
        step("projects.new", "#new-project-button"))), t, "projects.finish");
  }

  /**
   * The issue list. The filter step is the important one: the appjar defaults to showing only open
   * issues, exactly as Redmine does, and this demo keeps almost everything closed — so the saved
   * query is what the visitor is actually looking at, and it is worth saying so.
   */
  private static List<TourStep> issuesSteps(SerializableFunction<String, String> t) {
    return finish(new ArrayList<>(List.of(
        step("issues.grid", "#issue-list-grid"),
        step("issues.filters", "#issue-filters"),
        step("issues.queries", "#issue-savequery-button"),
        step("issues.groupby", "#issue-groupby"),
        step("issues.columns", "#issue-list-grid"),
        step("issues.detail", "#issue-list-grid"),
        step("issues.new", "#new-issue-button"))), t, "issues.finish");
  }

  private static List<TourStep> roadmapSteps(SerializableFunction<String, String> t) {
    return finish(new ArrayList<>(List.of(
        step("roadmap.intro", null),
        step("roadmap.versions", "#roadmap-versions"))), t, "roadmap.finish");
  }

  private static List<TourStep> ganttSteps(SerializableFunction<String, String> t) {
    return finish(new ArrayList<>(List.of(
        step("gantt.chart", "svar-gantt"),
        step("gantt.dependencies", "svar-gantt"),
        step("gantt.window", "#gantt-date-filters"),
        step("gantt.readonly", "#gantt-readonly-toggle"))), t, "gantt.finish");
  }

  private static List<TourStep> timeTrackingSteps(SerializableFunction<String, String> t) {
    return finish(new ArrayList<>(List.of(
        step("time.grid", "#time-entry-grid"),
        step("time.filters", "#time-filters"),
        step("time.groupby", "#time-groupby"),
        step("time.report", "#time-report-tab"))), t, "time.finish");
  }

  private static List<TourStep> timeReportSteps(SerializableFunction<String, String> t) {
    return finish(new ArrayList<>(List.of(
        step("report.grid", "#time-report-grid"),
        step("report.period", "#time-report-period"),
        step("report.groupby", "#time-report-groupby"))), t, "report.finish");
  }

  /**
   * The workflow editor. The transition matrix only appears once a role and a tracker are picked,
   * which is an action this tour deliberately does not perform on the visitor's behalf: it points
   * at the controls and explains what pressing Edit reveals.
   */
  private static List<TourStep> workflowSteps(SerializableFunction<String, String> t) {
    return finish(new ArrayList<>(List.of(
        step("workflow.intro", null),
        step("workflow.scope", "#workflow-scope-filters"),
        step("workflow.edit", "#workflow-edit-button"))), t, "workflow.finish");
  }

  // ---------------------------------------------------------------- step building

  /** What a step points at, before it knows its position in the tour. */
  private record StepSpec(String key, String attachTo) {}

  private static StepSpec step(String key, String attachTo) {
    return new StepSpec(key, attachTo);
  }

  /**
   * Appends the closing step and turns the specs into steps, giving each the buttons its position
   * calls for: the first has no Back, the last says Done. Doing it here rather than at each call
   * site keeps the tour definitions above to one readable line per step.
   */
  private static List<TourStep> finish(List<StepSpec> specs,
      SerializableFunction<String, String> t, String finishKey) {
    specs.add(step(finishKey, null));
    List<TourStep> built = new ArrayList<>(specs.size());
    for (int i = 0; i < specs.size(); i++) {
      built.add(build(specs.get(i), t, i == 0, i == specs.size() - 1));
    }
    return built;
  }

  private static TourStep build(StepSpec spec, SerializableFunction<String, String> t,
      boolean first, boolean last) {
    List<TourButton> buttons = new ArrayList<>();
    if (!first) {
      buttons.add(TourButton.builder().label(t.apply(KEY_PREFIX + "btn.back")).secondary(true)
          .type(TourButtonType.PREVIOUS).build());
    }
    buttons.add(TourButton.builder().label(t.apply(KEY_PREFIX + (last ? "btn.done" : "btn.next")))
        .type(TourButtonType.NEXT).build());

    TourStep.TourStepBuilder builder = TourStep.builder().id(spec.key().replace('.', '-'))
        .attachTo(spec.attachTo()).title(t.apply(KEY_PREFIX + spec.key() + ".title"))
        .content(t.apply(KEY_PREFIX + spec.key() + ".desc")).buttons(buttons);
    if (spec.attachTo() != null) {
      // To the right of the highlighted element, so the popover does not cover what it points at.
      builder.position("right");
    }
    return builder.build();
  }
}

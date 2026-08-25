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
package com.appjars.issuetracker.demo.views;

import com.appjars.issuetracker.business.service.ProjectService;
import com.appjars.issuetracker.demo.data.DemoDataSeeder;
import com.appjars.issuetracker.demo.service.UserSessionUtils;
import com.appjars.issuetracker.demo.views.tour.DemoTours;
import com.appjars.issuetracker.demo.views.tour.DemoTours.DemoTour;
import com.appjars.issuetracker.flow.component.IssueListComponent;
import com.appjars.issuetracker.flow.component.ProjectContextSwitcherComponent;
import com.appjars.issuetracker.flow.component.SearchField;
import com.appjars.issuetracker.flow.component.SpentTimeReportComponent;
import com.appjars.issuetracker.flow.view.CreateIssueView;
import com.appjars.issuetracker.flow.view.CustomFieldListView;
import com.appjars.issuetracker.flow.view.EnumerationListView;
import com.appjars.issuetracker.flow.view.GanttView;
import com.appjars.issuetracker.flow.view.GroupListView;
import com.appjars.issuetracker.flow.view.IssueStatusListView;
import com.appjars.issuetracker.flow.view.MyAccountView;
import com.appjars.issuetracker.flow.view.MyPageView;
import com.appjars.issuetracker.flow.view.ProjectActivityView;
import com.appjars.issuetracker.flow.view.ProjectCreateView;
import com.appjars.issuetracker.flow.view.ProjectListView;
import com.appjars.issuetracker.flow.view.RoadmapView;
import com.appjars.issuetracker.flow.view.RoleListView;
import com.appjars.issuetracker.flow.view.SearchView;
import com.appjars.issuetracker.flow.view.SettingsView;
import com.appjars.issuetracker.flow.view.TimeEntryForm;
import com.appjars.issuetracker.flow.view.TimeEntryListView;
import com.appjars.issuetracker.flow.view.TrackerListView;
import com.appjars.issuetracker.flow.view.UserListView;
import com.appjars.issuetracker.flow.view.WorkflowView;
import com.appjars.issuetracker.model.ProjectDto;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

// Anonymous so the public landing page (HomeView) can render inside this layout; every other
// view enforces its own @PermitAll and beforeEnter still reroutes anonymous users to login
@AnonymousAllowed
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainLayout extends AppLayout
    implements BeforeEnterObserver, AfterNavigationObserver {

  H2 viewTitle;
  UserSessionUtils userSessionUtils;
  final SearchField searchField;
  final ProjectContextSwitcherComponent projectContextSwitcher;

  /** Classes of every view in the current navigation chain; see {@link #rememberActiveViews}. */
  final Set<Class<?>> activeViews = new HashSet<>();

  /** Kept so each entry can be enabled only when that tour is reachable right now. */
  final Map<DemoTour, MenuItem> tourItems = new EnumMap<>(DemoTour.class);

  MenuItem thisScreenTour;

  public MainLayout(
      UserSessionUtils userSessionUtils,
      ProjectService projectService,
      SearchField searchField,
      ProjectContextSwitcherComponent projectContextSwitcher) {
    this.userSessionUtils = userSessionUtils;
    this.searchField = searchField;
    this.projectContextSwitcher = projectContextSwitcher;

    setPrimarySection(Section.DRAWER);
    addDrawerContent();
    addHeaderContent();
  }

  private void addHeaderContent() {
    DrawerToggle toggle = new DrawerToggle();
    toggle.getElement().setAttribute("aria-label", "Menu toggle");

    HorizontalLayout layout = new HorizontalLayout();
    layout.setAlignItems(Alignment.CENTER);
    layout.getStyle().setPaddingRight("var(--lumo-space-s)");
    viewTitle = new H2();

    viewTitle.getStyle().setFlexGrow("1");
    viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

    layout.add(viewTitle, searchField, projectContextSwitcher, createTourMenu());


    addToNavbar(true, toggle, viewTitle, layout);
  }

  /**
   * Puts the guided tours within reach from every screen, next to the project switcher, rather than
   * only on the landing page — so an evaluator who has navigated somewhere interesting can ask for
   * the tour of what they are looking at instead of going back home first.
   *
   * <p>This lives entirely in the demo: the appjar's views fill the content area, but the navbar
   * belongs to this layout, so nothing in the appjar has to change to make room for it.
   */
  private MenuBar createTourMenu() {
    MenuBar menu = new MenuBar();
    menu.setId("demo-tour-menu");
    menu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
    menu.setOpenOnHover(true);
    // Icon only, and not allowed to shrink: the navbar already carries the view title, the search
    // field and the project switcher, and with a text label next to the icon the menu bar ran out
    // of room and collapsed itself into an overflow button.
    menu.getStyle().setFlexShrink("0");

    MenuItem button = menu.addItem(VaadinIcon.MAP_MARKER.create());
    menu.setTooltipText(button, getTranslation("appjars.issuetracker.demo.home.tour.button"));
    SubMenu tours = button.getSubMenu();

    thisScreenTour = tours.addItem(getTranslation("appjars.issuetracker.demo.home.tour.thisscreen"),
        e -> {
          DemoTour tour = currentTour();
          if (tour != null) {
            runTour(tour);
          }
        });
    tours.addSeparator();

    for (DemoTour tour : DemoTour.values()) {
      if (DemoTours.requiresAdmin(tour) && !userSessionUtils.isAdmin()) {
        continue;
      }
      tourItems.put(tour, tours.addItem(DemoTours.menuLabel(tour, this::getTranslation),
          e -> startTour(tour)));
    }
    return menu;
  }

  /** Greys out the "this screen" entry on the screens that have no tour of their own. */
  private void refreshTourAvailability() {
    if (thisScreenTour != null) {
      thisScreenTour.setEnabled(currentTour() != null);
    }
  }

  private void onGlobalSearch(String input, boolean isAllProjects, ProjectDto project) {
    Map<String, List<String>> parameters = new HashMap<>();
    parameters.put("q", List.of(input));
    if(isAllProjects) {
      UI.getCurrent().navigate(
          SearchView.class,
          new QueryParameters(parameters)
      );
    }
    else {
      parameters.put("scope", List.of("subprojects"));
      UI.getCurrent().navigate(
          SearchView.class,
          new RouteParameters("identifier", project.getIdentifier()),
          new QueryParameters(parameters)
      );
    }
  }

  private void addDrawerContent() {
    H1 appName = new H1(getTranslation("appjars.issuetracker.demo.layout.drawertitle"));
    appName.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin",
        "var(--lumo-space-xs)");
    Header header = new Header(appName);

    SideNav navigation = createNavigation();

    Scroller scroller = new Scroller(navigation);

    VerticalLayout drawerContainer = new VerticalLayout(header, scroller, createFooter());
    drawerContainer.getStyle().set("position", "relative");
    drawerContainer.setSizeFull();
    drawerContainer.setAlignItems(Alignment.STRETCH);
    drawerContainer.getStyle().set("overflow", "hidden");
    drawerContainer.setPadding(false);
    drawerContainer.setSpacing(false);
    drawerContainer.setFlexGrow(1, scroller);
    addToDrawer(drawerContainer);

  }

  private SideNav createNavigation() {
    SideNav nav = new SideNav();

    SideNavItem homeItem = navItem("home", HomeView.class);
    homeItem.setPrefixComponent(VaadinIcon.HOME.create());
    nav.addItem(homeItem);

    SideNavItem issuetrackerItem =
        new SideNavItem(getTranslation("appjars.issuetracker.demo.menuitem.issuetrackerItem"));
    issuetrackerItem.setPrefixComponent(VaadinIcon.FILE_SEARCH.create());
    issuetrackerItem.setExpanded(true);
    SideNavItem adminItem =
        new SideNavItem(getTranslation("appjars.issuetracker.demo.menuitem.adminItem"));
    adminItem.setId("drawer-admin");
    adminItem.setExpanded(true);
    SideNavItem projectItem = navItem("projects", ProjectListView.class);

    adminItem.addItem(
        navItem("users", UserListView.class),
        navItem("groups", GroupListView.class),
        navItem("roles", RoleListView.class),
        navItem("trackers", TrackerListView.class),
        navItem("issuestatuses", IssueStatusListView.class),
        navItem("workflow", WorkflowView.class),
        navItem("customfields", CustomFieldListView.class),
        navItem("enumerations", EnumerationListView.class),
        navItem("settings", SettingsView.class));

    issuetrackerItem.addItem(
        navItem("mypage", MyPageView.class),
        projectItem,
        navItem("activity", ProjectActivityView.class),
        navItem("issues", IssueListComponent.class),
        navItem("gantt", GanttView.class),
        navItem("spenttime", TimeEntryListView.class));

    if (userSessionUtils.isAdmin()) {
      issuetrackerItem.addItem(adminItem);
    }

    issuetrackerItem.addItem(navItem("myaccount", MyAccountView.class));

    nav.addItem(issuetrackerItem);

    return nav;
  }

  private SideNavItem navItem(String key, Class<? extends Component> view) {
    SideNavItem item =
        new SideNavItem(getTranslation("appjars.issuetracker.demo.menuitem." + key), view);
    item.setId("drawer-" + key);
    return item;
  }

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    if (userSessionUtils.get().isEmpty() && !HomeView.class.equals(event.getNavigationTarget())) {
      event.rerouteTo(LoginView.class);
    }
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    viewTitle.setText(getCurrentPageTitle());
    // A tour is cancelled on every navigation, so abandoning one by navigating away leaves
    // nothing running.
    DemoTours.stopRunningTour(this);
    rememberActiveViews(event);
    refreshTourAvailability();
    startPendingTour();
  }

  /**
   * Records every view in the navigation chain, not just this layout's immediate content.
   *
   * <p>The appjar renders its project-scoped views inside a project layout that carries the
   * project's tabs, so for those routes {@code getContent()} is that tab layout and the view
   * itself sits one level further down. Looking at the whole chain is what makes a tour of, say,
   * the project issue list recognise that it has arrived.
   */
  private void rememberActiveViews(AfterNavigationEvent event) {
    activeViews.clear();
    event.getActiveChain().forEach(target -> activeViews.add(target.getClass()));
  }

  /**
   * Starts the tour that was picked before navigating, now that its view is on screen. Tours never
   * navigate themselves, so this is the only place one is started after a navigation and there is
   * nothing to chain: the pending tour is consumed and that is the end of it.
   */
  private void startPendingTour() {
    VaadinSession session = VaadinSession.getCurrent();
    if (session != null
        && session.getAttribute(DemoTours.PENDING_TOUR_ATTRIBUTE) instanceof DemoTour pending
        && showing(DemoTours.view(pending))) {
      session.setAttribute(DemoTours.PENDING_TOUR_ATTRIBUTE, null);
      runTour(pending);
    }
  }

  /** Runs the tour when its view is already on screen, otherwise stashes it and navigates there. */
  public void startTour(DemoTour tour) {
    Class<? extends Component> target = DemoTours.view(tour);
    if (showing(target)) {
      runTour(tour);
      return;
    }
    VaadinSession.getCurrent().setAttribute(DemoTours.PENDING_TOUR_ATTRIBUTE, tour);
    if (DemoTours.isProjectScoped(tour)) {
      UI.getCurrent().navigate(target,
          new RouteParameters("identifier", DemoDataSeeder.MAIN_PROJECT_IDENTIFIER));
    } else {
      UI.getCurrent().navigate(target);
    }
  }

  private void runTour(DemoTour tour) {
    DemoTours.start(tour, this, this::getTranslation, userSessionUtils.isAdmin());
  }

  /** A null view means the tour describes the layout itself, which is always on screen. */
  private boolean showing(Class<? extends Component> view) {
    return view == null || activeViews.contains(view);
  }

  /** The tour dedicated to the view being shown, or null when it has none. */
  private DemoTour currentTour() {
    for (DemoTour tour : DemoTour.values()) {
      Class<? extends Component> view = DemoTours.view(tour);
      if (view != null && activeViews.contains(view)) {
        return tour;
      }
    }
    return null;
  }

  private String getCurrentPageTitle() {
    PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
    return title == null ? "" : title.value();
  }

  private Footer createFooter() {
    Footer layout = new Footer();

    layout.getElement().getStyle().set("padding", "var(--lumo-space-s)");

    Optional<String> usernameOpt = userSessionUtils.getLoggedInUsername();
    if (usernameOpt.isPresent()) {

      Avatar avatar = new Avatar(usernameOpt.get());
      avatar.setThemeName("xsmall");
      avatar.getElement().setAttribute("tabindex", "-1");

      MenuBar userMenu = new MenuBar();
      userMenu.setThemeName("tertiary-inline contrast");

      MenuItem userName = userMenu.addItem("");
      Div div = new Div();
      div.add(avatar);
      div.add(usernameOpt.get());
      div.add(LumoIcon.DROPDOWN.create());
      div.getElement().getStyle().set("display", "flex");
      div.getElement().getStyle().set("align-items", "center");
      div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
      userName.add(div);
      userName
          .getSubMenu()
          .addItem(
              getTranslation("appjars.issuetracker.demo.layout.signout"),
              e -> userSessionUtils.logout());

      layout.add(userMenu);
    } else {
      Anchor loginLink =
          new Anchor("login", getTranslation("appjars.issuetracker.demo.layout.signin"));
      layout.add(loginLink);
    }

    return layout;
  }
}

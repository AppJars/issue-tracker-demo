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

import com.appjars.issuetracker.demo.service.UserSessionUtils;
import com.appjars.issuetracker.demo.views.tour.DemoTours;
import com.appjars.issuetracker.demo.views.tour.DemoTours.DemoTour;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * Public landing page of the demo: presents the appjar features, the demo credentials, the license
 * model and offers guided tours of the views.
 */
@SuppressWarnings("serial")
@AnonymousAllowed
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout implements HasDynamicTitle {

  private static final String KEY_PREFIX = "appjars.issuetracker.demo.home.";

  private static final String LOGO_PATH = "icons/icon-appjars-full.png";

  private static final String APPJARS_SITE_URL = "https://www.appjars.com";
  private static final String GITHUB_ORG_URL = "https://github.com/AppJars";
  private static final String GITHUB_REPO_URL = "https://github.com/AppJars/issue-tracker";

  private final UserSessionUtils userSessionUtils;

  public HomeView(UserSessionUtils userSessionUtils) {
    this.userSessionUtils = userSessionUtils;
    addClassName("home-view");
    add(createHero(), createFeaturesSection(), createTryItSection(), createLicenseSection(),
        createLinksSection());
    setAlignItems(Alignment.STRETCH);
  }

  private Component createHero() {
    Image logo = new Image(LOGO_PATH, t("hero.logoAlt"));
    logo.setWidth("144px");
    logo.setHeight("auto");
    logo.addClassName("home-logo");

    H1 title = new H1(t("hero.title"));
    Paragraph tagline = new Paragraph(t("hero.tagline"));
    tagline.addClassName("home-tagline");

    Div hero = new Div(logo, title, tagline);
    hero.setId("home-hero");
    hero.addClassName("home-hero");
    return hero;
  }

  private Component createFeaturesSection() {
    Div cards = new Div(
        featureCard(VaadinIcon.FOLDER_OPEN, "features.projects"),
        featureCard(VaadinIcon.TASKS, "features.issues"),
        featureCard(VaadinIcon.SITEMAP, "features.workflow"),
        featureCard(VaadinIcon.CLOCK, "features.time"),
        featureCard(VaadinIcon.FORM, "features.customfields"),
        featureCard(VaadinIcon.BOOK, "features.history"));
    cards.addClassName("home-features");

    return section("home-features", t("features.title"), cards);
  }

  private Card featureCard(VaadinIcon icon, String key) {
    Card card = new Card();
    card.addClassName("home-feature-card");
    Icon prefix = icon.create();
    prefix.addClassName("home-feature-icon");
    card.setHeaderPrefix(prefix);
    card.setTitle(t(key + ".title"));
    card.add(new Paragraph(t(key + ".desc")));
    return card;
  }

  /**
   * The credentials block lists every seeded account, not just the administrator: the roles have
   * genuinely different permissions, so signing in as each of them is the quickest way to see the
   * permission model do something.
   */
  private Component createTryItSection() {
    Paragraph intro = new Paragraph(t("tryit.intro"));

    Div credentials = new Div(
        credentialRow("admin / admin", t("tryit.admin")),
        credentialRow("mrivas / demo", t("tryit.pm")),
        credentialRow("dsantos / demo", t("tryit.developer")),
        credentialRow("lchen / demo", t("tryit.developerqa")),
        credentialRow("pnovak / demo", t("tryit.qa")));
    credentials.addClassName("home-credentials");

    Div actions = new Div(createTourMenu());
    actions.addClassName("home-actions");

    return section("home-tryit", t("tryit.title"), intro, credentials, actions);
  }

  private Div credentialRow(String credentials, String description) {
    Span code = new Span(credentials);
    code.addClassName("home-credential-code");
    Div row = new Div(code, new Span(description));
    row.addClassName("home-credential");
    return row;
  }

  /**
   * One entry per tour, for a visitor who has just landed and does not know what is on offer yet.
   * The same tours are reachable from the navbar on every screen; both go through
   * {@link MainLayout#startTour}, which owns the "navigate there first if we are not on it" logic.
   */
  private Component createTourMenu() {
    MenuBar menu = new MenuBar();
    menu.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
    menu.setOpenOnHover(true);
    SubMenu tours = menu
        .addItem(new Div(VaadinIcon.MAP_MARKER.create(), new Span(t("tour.button")))).getSubMenu();

    for (DemoTour tour : DemoTour.values()) {
      if (DemoTours.requiresAdmin(tour) && !userSessionUtils.isAdmin()) {
        continue;
      }
      tours.addItem(DemoTours.menuLabel(tour, this::getTranslation), e -> startTour(tour));
    }
    return menu;
  }

  private void startTour(DemoTour tour) {
    getParent().filter(MainLayout.class::isInstance).map(MainLayout.class::cast)
        .ifPresent(layout -> layout.startTour(tour));
  }

  private Component createLicenseSection() {
    Paragraph desc = new Paragraph(t("license.desc"));
    Anchor link = new Anchor(APPJARS_SITE_URL, t("license.link"));
    link.setTarget("_blank");
    return section("home-license", t("license.title"), desc, new Paragraph(link));
  }

  private Component createLinksSection() {
    Anchor github = new Anchor(GITHUB_ORG_URL, t("links.github"));
    github.setTarget("_blank");
    Anchor readme = new Anchor(GITHUB_REPO_URL, t("links.readme"));
    readme.setTarget("_blank");
    Div links = new Div(github, readme);
    links.addClassName("home-links");
    return section("home-links", t("links.title"), links);
  }

  private Div section(String id, String title, Component... content) {
    Div section = new Div();
    section.setId(id);
    section.addClassName("home-section");
    section.add(new H3(title));
    section.add(content);
    return section;
  }

  private String t(String key) {
    return getTranslation(KEY_PREFIX + key);
  }

  @Override
  public String getPageTitle() {
    return t("title");
  }
}

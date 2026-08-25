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
package com.appjars.issuetracker.demo;

import com.appjars.AppJarsAutoConfiguration;
import com.appjars.issuetracker.IssueTrackerAutoConfiguration;
import com.appjars.issuetracker.demo.views.MainLayout;
import com.appjars.issuetracker.flow.util.RouteConfigurer;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SuppressWarnings("serial")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(
    basePackageClasses = {IssueTrackerAutoConfiguration.class, AppJarsAutoConfiguration.class})
@EnableJpaRepositories(basePackageClasses = IssueTrackerAutoConfiguration.class)
@EnableVaadin(value = {"com.appjars.issuetracker.flow", "com.appjars.issuetracker.demo"})
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@StyleSheet("styles.css")
@NpmPackage(value = "@polymer/polymer", version = "3.5.2")
@PWA(name = "Issue Tracker Demo", shortName = "Issue Tracker Demo")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

  @Autowired RouteConfigurer routeConfigurer;

  // @PWA only declares the installed-app icons; the browser tab needs its own link tag.
  @Override
  public void configurePage(AppShellSettings settings) {
    settings.addFavIcon("icon", "icons/icon.png", "180x180");
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  public void configure() {
    routeConfigurer.setViewsRouterLayout(MainLayout.class);
  }
}

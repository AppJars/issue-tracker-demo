# Issue Tracker AppJars - Demo

A demo Spring Boot application showcasing the [Issue Tracker AppJar](https://github.com/AppJars/issue-tracker):
a Redmine-style project and issue tracker for Vaadin, with projects, trackers, workflows, Gantt
charts and time tracking.

The application opens on a public landing page that presents the features, the demo credentials and
a guided tour of each view, so you can see what the AppJar does before signing in.

## What comes preloaded

The first start seeds a dataset built to exercise every screen:

- **Three projects.** *Apollo Platform* carries the full dataset and is the one to look at first;
  *Apollo Mobile* is a subproject, so the project tree and the subproject search scope have
  something to show; *Legacy Portal* is closed, to show a retired project.
- **Five trackers** (Bug, Feature, Task, Support, Epic), each enabling a different set of standard
  fields, and **six issue statuses** spanning a real lifecycle.
- **A workflow matrix** with genuinely different transitions per role and tracker.
- **Around fifty issues** with subtasks under two epics, precedency relations so the Gantt shows
  dependency arrows, and a history built by actually walking each issue through its lifecycle — so
  the history tab attributes every change to the member who made it.
- **Four custom fields** (Severity, Regression, Story points, Customer) with values on the issues
  whose tracker they apply to.
- **A roadmap** with a released version, one in progress and one still being planned.
- **Roughly three months of logged time** spread across the team and seven activities, so the spent
  time report has data whichever way it is grouped.

Dates are relative to the day the dataset is created, so the Gantt and the reports stay meaningful
however long after this was written you run the demo.

The dataset is created only once. Restarting against a database that already has it leaves
everything — including anything you changed while trying the demo — untouched. To go back to the
original dataset, remove the database volume and let the containers recreate it.

## Prerequisites

| | |
|---|---|
| JDK | 21 or newer |
| Maven | 3.9 or newer (or the bundled `mvnw`, if you add one) |
| Docker | Required. Docker Compose v2 (`docker compose`) |

**Docker is not optional for this demo.** The database schema is created by the Redmine container's
own migrations; the application only reads and writes those tables. Without the containers there is
no schema and the application will fail to start.

## Running the demo

1. Start the database, Redmine and the fake mail server:

   ```bash
   docker compose -f docker-compose-redmine-postgres.yml up -d
   ```

   Wait until Redmine has finished migrating — `docker compose -f docker-compose-redmine-postgres.yml logs -f redmine`
   stops scrolling. The first run takes a couple of minutes.

2. Start the application (`spring-boot:run` is the default goal):

   ```bash
   mvn
   ```

   The first build downloads the Vaadin frontend toolchain, which also takes a few minutes.

3. Open <http://localhost:8080>. The landing page needs no account.

4. When you are done, stop the containers:

   ```bash
   docker compose -f docker-compose-redmine-postgres.yml down
   ```

   Add `-v` to that command to discard the database as well, so the next start seeds a fresh
   dataset.

## Signing in

Every account other than `admin` uses the password `demo`. Signing in as each of them is the
quickest way to see the permission model at work, since the application genuinely offers different
things to each role.

| Username | Password | Role | What this account can do |
|---|---|---|---|
| `admin` | `admin` | Administrator | Everything, including the administration screens |
| `mrivas` | `demo` | Project Manager | Manage members, versions, categories and every issue; move an issue to any status |
| `dsantos` | `demo` | Developer | Create and edit issues, log time; can push work forward and close it, but not reject it |
| `lchen` | `demo` | Developer on the platform, QA on mobile | Same as above on Apollo Platform; on Apollo Mobile only the QA transitions |
| `pnovak` | `demo` | QA Engineer | Review and close issues, log time; on Legacy Portal only a read-only Reporter |

## Using the demo

The drawer on the left lists every screen. **Administration** only appears for `admin`.

| Screen | What it shows |
|---|---|
| My page | Issues assigned to and reported by the signed-in user |
| Projects | The project tree, with status and the subproject hierarchy |
| Activities | The activity stream of a project |
| Issues | The issue list: filters, saved queries, grouping, column selection, and the issue detail with custom fields, watchers and history |
| Gantt | The issues on a timeline, with precedency arrows and a read-only toggle |
| Spent time | Logged time, and a second tab with the spent time report |
| My account | The signed-in user's own profile and preferences |

Administration screens (`admin` only):

| Screen | What it shows |
|---|---|
| Users | Accounts, their groups and their project memberships |
| Groups | User groups |
| Roles | Roles and their permissions |
| Trackers | Trackers and the standard fields each one enables |
| Issue statuses | The statuses issues can be in |
| Workflow | The transition matrix, per role and tracker |
| Custom fields | Custom fields for projects, issues and time entries |
| Enumerations | Priorities, time-tracking activities and document categories |
| Settings | Application-wide settings |

A **Roadmap** screen is reached from inside a project rather than from the drawer.

### Guided tours

The map-marker button at the top right of every screen opens the tour menu. *This screen* runs the
tour of whatever you are looking at; the entries below it run a specific one, navigating there
first if needed. Every tour is read-only: it points at data that is already there and explains it,
and never changes anything.

Tours are available for the drawer itself and for Projects, Issues, Roadmap, Gantt, Spent time, the
spent time report and Workflow. The remaining screens have no tour yet.

## Configuration

Everything below lives in [`src/main/resources/application.properties`](src/main/resources/application.properties),
which lists the AppJar's properties with the values it defaults to. Note that leaving one of them
blank does not fall back to the default — it overrides it with an empty string.

### Issue Tracker AppJar

| Property | Default | What it does |
|---|---|---|
| `appjars.issuetracker.auth.enabled` | `true` | Whether the AppJar owns authentication: passwords, the login form and user creation. Set to `false` when the host application manages credentials externally |
| `appjars.issuetracker.attachment.file-path` | `files/` | Directory attachments are written to, relative to the working directory |
| `appjars.issuetracker.authuser.error.fallback-route` | `/` | Where an authenticated user with no Issue Tracker account is sent |
| `appjars.issuetracker.url.base` | `http://localhost:8080/` | Absolute base URL used to build the links inside notification emails |
| `appjars.issuetracker.url.views.issue` | `it/issues/:id` | Issue route the emails link to; `:id` is replaced with the issue id |
| `appjars.issuetracker.url.views.users-profile` | `it/users/:id` | User route the emails link to |
| `appjars.issuetracker.url.views.settings` | `it/settings` | Settings route the emails link to |

### Database

| Property | Value in this demo |
|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5440/redmine` |
| `spring.datasource.username` / `password` | `redmine` / `secret` |

These match the `db` service in `docker-compose-redmine-postgres.yml`. Change both together.

### Mail

| Property | Value in this demo |
|---|---|
| `spring.mail.host` / `port` | `localhost` / `1025` |

Notification emails go to Mailpit instead of being delivered. Read them at
<http://localhost:8025>.

## Services started by Compose

| Service | Port | What it is for |
|---|---|---|
| `db` | 5440 | PostgreSQL. The database the application uses |
| `redmine` | 8081 | Creates and migrates the schema. Not part of the demo itself |
| `mailpit` | 1025, 8025 | Captures outgoing mail and provides a web inbox |

## License

Two different licenses apply, and it is worth keeping them apart.

**This demo** is licensed under the Apache License, Version 2.0 — see [LICENSE.txt](LICENSE.txt). Take the
code, copy from it, build on it.

**The Issue Tracker AppJar** the demo depends on is a commercial product. It runs here in free
mode: every feature is fully functional, limited to 5 users, 5 open issues and 10 time entries per
day in total. A full license removes those limits — see <https://www.appjars.com>.

The dataset is built to live within the free-mode limits: it uses exactly five accounts, and it
deliberately leaves only three issues open — the rest are closed or rejected, as a mostly-delivered
project would be — so that you can still create and edit issues while trying the demo. The three
open ones are assigned to `admin`, which is also what gives My page something to show.

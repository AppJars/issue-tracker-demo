---
-- #%L
-- Issue Tracker AppJars - Demo
-- %%
-- Copyright (C) 2023 - 2026 AppJars
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
-- This script only bootstraps what has to exist before the application can serve a request: the
-- built-in Administrator role and the appjar's default list settings. Everything a visitor
-- actually sees -- priorities, activities, statuses, trackers, users, roles, projects, issues and
-- logged time -- is created afterwards by DemoDataSeeder through the appjar's own services, so
-- that the demo data goes through the same validation as data entered by hand and does not have
-- to be kept in sync with the database schema by hand.

--Roles
INSERT INTO public.roles (id, "name", "position", assignable, builtin, permissions, issues_visibility, users_visibility, time_entries_visibility, all_roles_managed, settings) VALUES(3, 'Administrator', 1, true, 0, '---
- :add_project
- :edit_project
- :close_project
- :delete_project
- :select_project_publicity
- :select_project_modules
- :manage_members
- :manage_versions
- :add_subprojects
- :manage_public_queries
- :save_queries
- :view_messages
- :add_messages
- :edit_messages
- :edit_own_messages
- :delete_messages
- :delete_own_messages
- :view_message_watchers
- :add_message_watchers
- :delete_message_watchers
- :manage_boards
- :view_calendar
- :view_documents
- :add_documents
- :edit_documents
- :delete_documents
- :view_files
- :manage_files
- :view_gantt
- :view_issues
- :add_issues
- :edit_issues
- :edit_own_issues
- :copy_issues
- :manage_issue_relations
- :manage_subtasks
- :set_issues_private
- :set_own_issues_private
- :add_issue_notes
- :edit_issue_notes
- :edit_own_issue_notes
- :view_private_notes
- :set_notes_private
- :delete_issues
- :view_issue_watchers
- :add_issue_watchers
- :delete_issue_watchers
- :import_issues
- :manage_categories
- :view_news
- :manage_news
- :comment_news
- :view_changesets
- :browse_repository
- :commit_access
- :manage_related_issues
- :manage_repository
- :view_time_entries
- :log_time
- :edit_time_entries
- :edit_own_time_entries
- :manage_project_activities
- :log_time_for_other_users
- :import_time_entries
- :view_wiki_pages
- :view_wiki_edits
- :export_wiki_pages
- :edit_wiki_pages
- :rename_wiki_pages
- :delete_wiki_pages
- :delete_wiki_pages_attachments
- :view_wiki_page_watchers
- :add_wiki_page_watchers
- :delete_wiki_page_watchers
- :protect_wiki_pages
- :manage_wiki
', 'default', 'all', 'all', true, '---
permissions_all_trackers:
  view_issues: ''1''
  add_issues: ''1''
  edit_issues: ''1''
  add_issue_notes: ''1''
  delete_issues: ''1''
permissions_tracker_ids:
  view_issues: []
  add_issues: []
  edit_issues: []
  add_issue_notes: []
  delete_issues: []
') ON CONFLICT DO NOTHING;
INSERT INTO public.roles_managed_roles (role_id, managed_role_id) VALUES(3, 3) ON CONFLICT DO NOTHING;

--Settings defaults
INSERT INTO public.settings ("name", value) VALUES
  ('time_entry_list_defaults', ':column_names:
- spent_on
- activity
- user
- hours
- comments
- issue
- project
:totalable_names: [
  ]
'),
  ('issue_list_default_columns', '- tracker
- status
- assigned_to
- priority
- author
- subject
'),
  ('project_list_defaults', ':column_names:
- name
- short_description
- identifier
- is_public
') ON CONFLICT DO NOTHING;
--sequence syncing
-- The rows above are inserted with explicit ids, which leaves the sequences behind. Everything
-- DemoDataSeeder creates afterwards draws from these sequences, so they have to be moved past the
-- highest id already in use. COALESCE covers the tables this script leaves empty, where MAX(id)
-- is null on a fresh database and setval would otherwise fail.

--users
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM users));
--user_preferences
SELECT setval(pg_get_serial_sequence('user_preferences', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM user_preferences));
--enumerations
SELECT setval(pg_get_serial_sequence('enumerations', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM enumerations));
--issue_statuses
SELECT setval(pg_get_serial_sequence('issue_statuses', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM issue_statuses));
--trackers
SELECT setval(pg_get_serial_sequence('trackers', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM trackers));
--roles
SELECT setval(pg_get_serial_sequence('roles', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM roles));
--projects
SELECT setval(pg_get_serial_sequence('projects', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM projects));
--enabled_modules
SELECT setval(pg_get_serial_sequence('enabled_modules', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM enabled_modules));
--members
SELECT setval(pg_get_serial_sequence('members', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM members));
--member_roles
SELECT setval(pg_get_serial_sequence('member_roles', 'id'), (SELECT COALESCE(MAX(id), 0)+1 FROM member_roles));
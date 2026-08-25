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
UPDATE public.users SET login='admin', hashed_password='349390ce1c30d62d5d5b5aa2651ffa42d47319ee', firstname='Redmine', lastname='Admin', "admin"=true, status=1, last_login_on=NULL, "language"='', created_on='2024-09-24 14:03:43.069', updated_on='2024-09-24 14:03:43.069', "type"='User', mail_notification='all', salt='401d45557d4daee86ba50ef475c1434b', must_change_passwd=false, passwd_changed_on=NULL, twofa_scheme=NULL, twofa_totp_key=NULL, twofa_totp_last_used_at=NULL, twofa_required=false WHERE id=1;
INSERT INTO public.users (id, login, hashed_password, firstname, lastname, "admin", status, last_login_on, "language", created_on, updated_on, "type", mail_notification, salt, must_change_passwd, passwd_changed_on, twofa_scheme, twofa_totp_key, twofa_totp_last_used_at, twofa_required) VALUES(5, '', '', '', 'Administrators', false, 1, NULL, '', '2024-09-24 13:43:20.889', '2024-09-24 13:43:20.889', 'Group', '', NULL, false, NULL, NULL, NULL, NULL, false) ON CONFLICT DO NOTHING;
INSERT INTO public.user_preferences (id, user_id, "others", hide_mail, time_zone) VALUES(1, 1, '---
:no_self_notified: ''1''
:auto_watch_on:
- ''''
- issue_created
- issue_contributed_to
:my_page_layout:
  left:
  - issuesassignedtome
  right:
  - issuesreportedbyme
:my_page_settings: {}
:comments_sorting: asc
:warn_on_leaving_unsaved: ''1''
:textarea_font: ''''
:recently_used_projects: 3
:history_default_tab: notes
:toolbar_language_options: c,cpp,csharp,css,diff,go,groovy,html,java,javascript,objc,perl,php,python,r,ruby,sass,scala,shell,sql,swift,xml,yaml
:default_issue_query: ''''
:default_project_query: ''''
', true, '') ON CONFLICT DO NOTHING;
INSERT INTO public.groups_users (group_id, user_id) VALUES(5, 1) ON CONFLICT DO NOTHING;
-- The admin's project memberships are created by DemoDataSeeder together with the projects
-- themselves: this script runs before any project exists.

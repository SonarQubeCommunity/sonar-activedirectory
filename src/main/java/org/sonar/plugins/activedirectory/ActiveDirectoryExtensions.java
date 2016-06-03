/*
 * SonarQube Active Directory Plugin
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.activedirectory;

import org.sonar.api.ExtensionProvider;
import org.sonar.api.ServerExtension;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.activedirectory.windows.WindowsAuthenticationHelper;
import org.sonar.plugins.activedirectory.windows.WindowsSecurityRealm;
import org.sonar.plugins.activedirectory.windows.auth.WindowsAuthSettings;
import org.sonar.plugins.activedirectory.windows.auth.servlet.WindowsLogoutFilter;
import org.sonar.plugins.activedirectory.windows.sso.servlet.SsoAuthenticationFilter;
import org.sonar.plugins.activedirectory.windows.sso.servlet.SsoValidationFilter;

import static java.util.Arrays.asList;
import static org.sonar.api.CoreProperties.CORE_AUTHENTICATOR_REALM;

public class ActiveDirectoryExtensions extends ExtensionProvider implements ServerExtension {

  private Logger LOG = Loggers.get(ActiveDirectoryExtensions.class);

  private final System2 system2;
  private final Settings settings;

  public ActiveDirectoryExtensions(System2 system2, Settings settings) {
    this.system2 = system2;
    this.settings = settings;
  }

  @Override
  public Object provide() {
    if (system2.isOsWindows() && WindowsSecurityRealm.NAME.equals(settings.getString(CORE_AUTHENTICATOR_REALM))) {
      return asList(
        WindowsSecurityRealm.class,
        WindowsAuthenticationHelper.class,
        WindowsAuthSettings.class,
        SsoAuthenticationFilter.class,
        SsoValidationFilter.class,
        WindowsLogoutFilter.class);
    }
    LOG.warn("Active Directory plugin is installed, while the OS is not Windows.");

    // If the realm is not set to use this plugin, do not load filter extensions
    return asList(
      WindowsSecurityRealm.class,
      WindowsAuthenticationHelper.class,
      WindowsAuthSettings.class);
  }

}

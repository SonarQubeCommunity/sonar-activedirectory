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
package org.sonar.plugins.activedirectoy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.sonar.api.ExtensionProvider;
import org.sonar.api.ServerExtension;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.activedirectoy.windows.WindowsAuthenticationHelper;
import org.sonar.plugins.activedirectoy.windows.WindowsSecurityRealm;
import org.sonar.plugins.activedirectoy.windows.auth.WindowsAuthSettings;
import org.sonar.plugins.activedirectoy.windows.auth.servlet.WindowsLogoutFilter;
import org.sonar.plugins.activedirectoy.windows.sso.servlet.SsoAuthenticationFilter;
import org.sonar.plugins.activedirectoy.windows.sso.servlet.SsoValidationFilter;

public class ActiveDirectoryExtensions extends ExtensionProvider implements ServerExtension {

  private Logger LOG = Loggers.get(ActiveDirectoryExtensions.class);

  private final System2 system2;

  ActiveDirectoryExtensions(System2 system2) {
    this.system2 = system2;
  }

  @Override
  public Object provide() {
    if (system2.isOsWindows()) {
      return getWindowsAuthExtensions();
    } else {
      LOG.warn("Active Directory plugin is installed, while the OS is not Windows.");
    }
    return Collections.emptyList();
  }

  private List<Class<?>> getWindowsAuthExtensions() {
    return Arrays.asList(
      WindowsSecurityRealm.class,
      WindowsAuthenticationHelper.class,
      WindowsAuthSettings.class,
      SsoAuthenticationFilter.class,
      SsoValidationFilter.class,
      WindowsLogoutFilter.class);
  }

}

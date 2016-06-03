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

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.System2;
import org.sonar.plugins.activedirectory.windows.WindowsAuthenticationHelper;
import org.sonar.plugins.activedirectory.windows.WindowsSecurityRealm;
import org.sonar.plugins.activedirectory.windows.auth.WindowsAuthSettings;
import org.sonar.plugins.activedirectory.windows.auth.servlet.WindowsLogoutFilter;
import org.sonar.plugins.activedirectory.windows.sso.servlet.SsoAuthenticationFilter;
import org.sonar.plugins.activedirectory.windows.sso.servlet.SsoValidationFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonar.api.CoreProperties.CORE_AUTHENTICATOR_REALM;

public class ActiveDirectoryExtensionsTest {

  System2 system2 = mock(System2.class);
  Settings settings = new Settings();

  @Test
  public void provideTests() {
    setReamToActiveDirectory();
    ActiveDirectoryExtensions activeDirectoryExtensions = new ActiveDirectoryExtensions(system2, settings);

    Object ldapExtensionsObject = activeDirectoryExtensions.provide();
    assertThat(ldapExtensionsObject).isNotNull();
  }

  @Test
  public void getExtensionsDefaultOnWindowsTests() {
    setReamToActiveDirectory();
    this.runGetExtensionsDefaultTest(true, this.getExpectedWindowsExtensions());
  }

  @Test
  public void getExtensionsDefaultOnNonWindowsOsTests() {
    setReamToActiveDirectory();
    this.runGetExtensionsDefaultTest(false, getExtensionsWithoutFilters());
  }

  @Test
  public void getExtensionsForWindowsSecurity() {
    setReamToActiveDirectory();
    this.runGetExtensionsTest(true, this.getExpectedWindowsExtensions());
  }

  @Test
  public void filter_extensions_are_not_loaded_when_realm_is_not_active_directory() throws Exception {
    settings.setProperty(CORE_AUTHENTICATOR_REALM, "LDAP");
    this.runGetExtensionsDefaultTest(true, getExtensionsWithoutFilters());
  }

  private void runGetExtensionsDefaultTest(boolean isOperatingSystemWindows, List<Class<?>> expectedExtensions) {
    when(system2.isOsWindows()).thenReturn(isOperatingSystemWindows);
    ActiveDirectoryExtensions activeDirectoryExtensions = new ActiveDirectoryExtensions(system2, settings);

    List<Class<?>> extensions = (List<Class<?>>) activeDirectoryExtensions.provide();

    assertThat(extensions).isNotNull().hasSameElementsAs(expectedExtensions);
  }

  private void runGetExtensionsTest(boolean isOperatingSystemWindows, List<Class<?>> expectedExtensions) {
    when(system2.isOsWindows()).thenReturn(isOperatingSystemWindows);

    ActiveDirectoryExtensions activeDirectoryExtensions = new ActiveDirectoryExtensions(system2, settings);

    List<Class<?>> extensions = (List<Class<?>>) activeDirectoryExtensions.provide();
    assertThat(extensions).isNotNull().hasSameElementsAs(expectedExtensions);
  }

  private void setReamToActiveDirectory(){
    settings.setProperty(CORE_AUTHENTICATOR_REALM, "ACTIVE_DIRECTORY");
  }

  private List<Class<?>> getExpectedWindowsExtensions() {
    return Arrays.asList(
      WindowsSecurityRealm.class,
      WindowsAuthenticationHelper.class,
      WindowsAuthSettings.class,
      SsoAuthenticationFilter.class,
      SsoValidationFilter.class,
      WindowsLogoutFilter.class);
  }

  private List<Class<?>> getExtensionsWithoutFilters() {
    return Arrays.asList(
      WindowsSecurityRealm.class,
      WindowsAuthenticationHelper.class,
      WindowsAuthSettings.class);
  }
}

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
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.plugins.activedirectoy.windows.WindowsAuthenticationHelper;
import org.sonar.plugins.activedirectoy.windows.WindowsSecurityRealm;
import org.sonar.plugins.activedirectoy.windows.auth.WindowsAuthSettings;
import org.sonar.plugins.activedirectoy.windows.auth.servlet.WindowsLogoutFilter;
import org.sonar.plugins.activedirectoy.windows.sso.servlet.SsoAuthenticationFilter;
import org.sonar.plugins.activedirectoy.windows.sso.servlet.SsoValidationFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActiveDirectoryExtensionsTest {

  System2 system2 = mock(System2.class);

  @Test
  public void provideTests() {
    ActiveDirectoryExtensions activeDirectoryExtensions = new ActiveDirectoryExtensions(system2);

    Object ldapExtensionsObject = activeDirectoryExtensions.provide();
    assertThat(ldapExtensionsObject).isNotNull();
  }

  @Test
  public void getExtensionsDefaultOnWindowsTests() {
    this.runGetExtensionsDefaultTest(true, this.getExpectedWindowsExtensions());
  }

  @Test
  public void getExtensionsDefaultOnNonWindowsOsTests() {
    this.runGetExtensionsDefaultTest(false, Collections.emptyList());
  }

  @Test
  public void getExtensionsForWindowsSecurity() {
    this.runGetExtensionsTest(true, this.getExpectedWindowsExtensions());
  }

  private void runGetExtensionsDefaultTest(boolean isOperatingSystemWindows, List<Class<?>> expectedExtensions) {
    when(system2.isOsWindows()).thenReturn(isOperatingSystemWindows);
    ActiveDirectoryExtensions activeDirectoryExtensions = new ActiveDirectoryExtensions(system2);

    List<Class<?>> extensions = (List<Class<?>>) activeDirectoryExtensions.provide();

    assertThat(extensions).isNotNull().hasSameElementsAs(expectedExtensions);
  }

  private void runGetExtensionsTest(boolean isOperatingSystemWindows, List<Class<?>> expectedExtensions) {
    when(system2.isOsWindows()).thenReturn(isOperatingSystemWindows);

    ActiveDirectoryExtensions activeDirectoryExtensions = new ActiveDirectoryExtensions(system2);

    List<Class<?>> extensions = (List<Class<?>>) activeDirectoryExtensions.provide();
    assertThat(extensions).isNotNull().hasSameElementsAs(expectedExtensions);
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
}

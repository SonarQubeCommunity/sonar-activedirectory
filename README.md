# Active Directory Plugin

## Description ##

This plugin allows the delegation of SonarQube authentication and authorization to Microsoft Active Directory.
It automatically logs in user using Single Sign On (SSO) with Active Directory Credentials in Microsoft Active Directory Environments. Active user's windows domain credentials are used to login to SonarQube.

During the first authentication trial, the SonarQube database is automatically populated with the new user.
Each time a user logs into SonarQube, the username, the email and the groups this user belongs to that are refreshed in the SonarQube database.


## Installation ##

1. Install the plugin through the [Update Center](http://docs.sonarqube.org/display/SONAR/Update+Center) or download it into the *SONARQUBE_HOME/extensions/plugins* directory
1. Restart the SonarQube server


## Usage ##

1. Configure the plugin by editing the SONARQUBE_HOME/conf/sonar.properties file (see table below)
1. Restart the SonarQube server
1. Single Sign On (SSO) will be performed on hitting any SonarQube URL other than /sessions/login
1. On log out users will be presented login page (/sessions/login), where they can choose to login as local user or a domain user by passing appropriate credentials


## Pre-requisites for Negotiate Protocol in SSO ##

For negotiate authentication to work in SSO the following steps need to be followed:

1. Follow the instructions for your browser present in the following link. Waffle link: [Configuring Browsers (IE/Firefox)](https://github.com/dblock/waffle/blob/master/Docs/ConfiguringBrowsers.md)
1. Make sure the user has privileges for Kerberos Delegation : ``setspn -L username``
  * To add privileges for current user run : ``setspn -S HTTP/<machine>:<port> <machine>``
  * Ex: ``setspn -S ContosoDev:9000 ContosoDev``
1. Ensure the SonarQube server is running as a service (NT service)  using a service account or domain account


## General Configuration ##

Property | Description | Default value | Mandatory | Example
---------| ----------- | ------------- | --------- | -------
sonar.security.realm|To first try to authenticate against the external sytem. If the external system is not reachable or if the user is not defined in the external system, the authentication will be performed through the SonarQube internal system.|None|Yes|ACTIVE_DIRECTORY (Only possible value)
ldap.windows.group.downcase|Set to true to return the group names in lowercase. Note that this setting will be ignored if ldap.windows.compatibilityMode is set to true|true|No|true or false
ldap.windows.sso.protocols|Protocol to be used during SSO for user authentication. Eg. "Negotiate NTLM". Note: It is recommended to use Negotiate protocol in production environments. Kerberos configuration steps have to be completed before using Negotiate protocol for authentication see [Pre-requisites for Negotiate Protocol in SSO](http://docs.sonarqube.org/display/PLUG/Pre-requisites+for+Negotiate+Protocol+in+SSO)|NTLM|No|NTML, Negotiate
ldap.windows.compatibilityMode|Property to tell the plugin to run windows auth in compatibility mode. I.e. it will support all the : Authorization done using user-id/group-id in 1.4 version of the plugin, Customization done in user profile|false|no|true or false
ldap.group.idAttribute|Property used to specify the attribute to be used for returning the list of user groups in the compatibility mode.|cn|No|sAMAccountName

## Configuration Example ##
```
# Active Directory configuration
sonar.security.realm=ACTIVE_DIRECTORY

#Following are set by default and need not be configured explicitly
#ldap.windows.groups.downcase=true
#ldap.windows.sso.protocols=NTLM

#ldap.windows.compatibilityMode=false
#ldap.group.idAttribute=cn

```

## Group Mapping ##

Only [groups](http://identitycontrol.blogspot.fr/2007/07/static-vs-dynamic-ldap-groups.html) are supported. Only [static groups](http://identitycontrol.blogspot.fr/2007/07/static-vs-dynamic-ldap-groups.html) are supported (not [dynamic groups](http://identitycontrol.blogspot.fr/2007/07/static-vs-dynamic-ldap-groups.html)).

Membership in Active Directory will override any membership locally configured in SonarQube. Active Directory becomes the one and only place to manage group membership (and the info is fetched each time the user logs in).
For the delegation of authorization, [groups must be first defined in SonarQube](http://docs.sonarqube.org/display/SONAR/Authorization).

## Active Directory Group Support ##

Below table illustrates the support for different types of active directory groups based on different modes of the plugin.

Groups type | Non-Compatibility Mode | Compatibility Mode
----------- | ---------------------- | ------------------
Domain Security Groups|Yes|Yes
Domain Nested Security Groups|Yes|No
Cross-domain Security Groups|Yes|No

> __Groupname format__
>
> groups read in AD have the groupname@domain syntax. Note the lower case as ldap.windows.group.downcase defaults to true.
> Since [groups must be defined in SonarQube](http://docs.sonarqube.org/display/SONAR/Authorization) for Group Mapping to work, make sure to define them in this groupname@domain form.


## Existing LDAP Plugin Users ##

> __Username format__
>
> usernames have the following format: username@domain

If you have an existing setup of LDAP Plugin in an Active Directory environment, you have two options.

Option 1: Move to the new model. (Recommended)
1. Remove all the configurations that you have setup for LDAP plugin in sonar.properties.
1. Add domain groups in SonarQube
1. Specify global and project permissions for the domain groups
1. If any user has customizations in their profile, ask them to re-apply them after logging in with domain credentials.

Option 2: Keep using the old model and add the following to the sonar.properties
```
# LDAP configuration
sonar.security.realm=ACTIVE_DIRECTORY

ldap.windows.compatibilityMode = true
```

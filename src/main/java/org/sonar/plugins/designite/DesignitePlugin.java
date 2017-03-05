/*
 * SonarQube Designite Plugin
 * Copyright (C) 2017 Designite
 * contact@designite-tools.com
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.designite;

import com.google.common.collect.ImmutableList;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public class DesignitePlugin extends SonarPlugin 
{
  public static final String LANGUAGE_KEY = "cs";
  private static final String CATEGORY = "Designite";

  public static final String REPOSITORY_KEY = "designite";
  public static final String REPOSITORY_NAME = "Designite";

  // Rules key use for define rules and it shows on gui  
  public static final String DESIGNITE_PATH_PROPERTY_KEY = "sonar.cs.designite.path";
  //this key use for specify .sln file path
  //public static final String DESIGNITE_PROJECT_PATH_PROPERTY_KEY = "sonar.cs.designite.projectFilePath";
  public static final String TIMEOUT_PROPERTY_KEY = "sonar.cs.designite.timeoutMinutes";
  
  public List getExtensions() 
  {
    ImmutableList.Builder builder = ImmutableList.builder();

    builder.add(
      DesigniteConfiguration.class,
      DesigniteRulesDefinition.class,
      DesigniteSensor.class);

    builder.addAll(pluginProperties());

    return builder.build();
  }

  private static ImmutableList<PropertyDefinition> pluginProperties() 
  {
    return ImmutableList.of(
        PropertyDefinition.builder(DESIGNITE_PATH_PROPERTY_KEY)
        .name("Path to DesigniteConsole.exe")
        .description("Provide the absolute path to DesigniteConsole.exe. Example: C:/DesigniteConsole.exe")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),
      PropertyDefinition.builder(TIMEOUT_PROPERTY_KEY)
        .name("Designite execution timeout")
        .description("Time in minutes after which Designite execution should be interrupted if not finished")
        .defaultValue("10")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .type(PropertyType.INTEGER)
        .build()     
      /*PropertyDefinition.builder(DESIGNITE_PROJECT_PATH_PROPERTY_KEY)
        .name("Projects' solution (.sln) file")
        .description("Provide the aboslute path of the sln file of the projects to be analyzed. Example: C:/ProjectX/ProjectX.sln")
        .category(CATEGORY)
        .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build()*/);
  }

}

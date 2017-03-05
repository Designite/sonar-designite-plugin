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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issuable.IssueBuilder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRule;

public class DesigniteReportParserCallback implements ReportParserCallback
{
  private static final Logger LOG = LoggerFactory.getLogger(DesigniteReportParserCallback.class);
  private RulesProfile rulesProfile;
  private FileSystem fileSystem;
  private ResourcePerspectives perspectives;
  
  public DesigniteReportParserCallback(RulesProfile rulesProfile, FileSystem fileSystem, ResourcePerspectives perspectives) 
  {
	  this.rulesProfile = rulesProfile;
	  this.fileSystem = fileSystem;
	  this.perspectives = perspectives;
  }

  public void onIssue(String ruleKey, String description, String filePath)
  {
	  ActiveRule rule = rulesProfile.getActiveRule(DesignitePlugin.REPOSITORY_KEY, ruleKey);
	  
	  if (rule == null) 
	  {
		  logSkippedIssue("The rule is disabled in the current quality profile", ruleKey, description, filePath);
            return;
	  }
	
	  InputFile inputFile = fileSystem.inputFile(
			  fileSystem.predicates().and(fileSystem.predicates().hasAbsolutePath(filePath),
					  fileSystem.predicates().hasType(Type.MAIN)));

	  if (inputFile == null) 
	  {
		  logSkippedIssue("The file is not imported in SonarQube", ruleKey, description, filePath);
		  return;
	  }

      Issuable issuable = perspectives.as(Issuable.class, inputFile);
      if (issuable == null) 
      {
    	  logSkippedIssue("There are no issuable found for the file", ruleKey, description, filePath);
          return;
      }

      //add issue into sonarqube dashboard 
      IssueBuilder builder = issuable.newIssueBuilder();
      builder.ruleKey(RuleKey.of(DesignitePlugin.REPOSITORY_KEY, ruleKey));
      builder.message(description);
      issuable.addIssue(builder.build());
  }

  private void logSkippedIssue(String reason, String ruleKey, String description, String filePath) 
  {
	  LOG.info("Skipping Designite issue on file " + filePath + " on rule " + ruleKey + " with following description: " + description + " because " + reason);
  }

}

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

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DesigniteSensor implements Sensor 
{
  private static final Logger LOG = LoggerFactory.getLogger(DesigniteSensor.class);
 
  private final DesigniteConfiguration configuration;
  private final FileSystem fileSystem;
  private final RulesProfile rulesProfile;
  private final ResourcePerspectives perspectives;

  private String projectName; 
  //private String projectPath;
  
  public DesigniteSensor(DesigniteConfiguration conf, FileSystem fs, RulesProfile profile, ResourcePerspectives perspectives) 
  {
    this.configuration = conf;
    this.fileSystem = fs;
    this.rulesProfile = profile;
    this.perspectives = perspectives;
  }

  public boolean shouldExecuteOnProject(Project project) 
  {
    boolean shouldExecute;  
    
    if (!hasFilesToAnalyze()) 
      shouldExecute = false;
    else if (rulesProfile.getActiveRulesByRepository(DesignitePlugin.REPOSITORY_KEY).isEmpty())
    {
      LOG.info("All Designite rules are disabled, skipping its execution.");
      shouldExecute = false;
    } 
    else 
    {
      shouldExecute = true;
    }

    return shouldExecute;
  }

  private boolean hasFilesToAnalyze() 
  {
    return fileSystem.files(fileSystem.predicates().hasLanguage(DesignitePlugin.LANGUAGE_KEY)).iterator().hasNext();
  }
  
  
  public void analyse(Project project, SensorContext context) 
  {
	  projectName=project.getName();

	  analyze(context, new DesigniteExecutor(), project.path());
  }
  
  @VisibleForTesting
  void analyze(SensorContext context, DesigniteExecutor executor, String projectPath) 
  {
	  String  workingFolderPath = fileSystem.workDir().getAbsolutePath();
	  
      File reportFile = new File(workingFolderPath, "designite-report.xml");
      String reportFilePath = reportFile.getAbsolutePath();
      String projectFullPath = fileSystem.baseDir()+ "\\" + projectName + ".csproj";
      
      String batchFile = writeBatchFile(workingFolderPath, projectFullPath);
      
      //executor.execute(configuration.designitePath(), configuration.designiteProjectPath(), 
    	//	  reportFilePath, configuration.timeout());
      executor.execute(configuration.designitePath(), batchFile, 
    	    		  reportFilePath, configuration.timeout());
    		  
    //Let us parse the output 
      DesigniteReportParser reportParser = new DesigniteReportParser(
    		  new DesigniteReportParserCallback(rulesProfile, fileSystem, perspectives));
      reportParser.parse(reportFile);
  }

private String writeBatchFile(String workingFolderPath, String projectFullPath) {
	String path = workingFolderPath +  "\\prjBatch.batch";
	FileWriter fw;
	try {
		fw = new FileWriter(path);
	
	fw.write("[Projects]\n" + projectFullPath); 
 
	fw.close();
	} catch (IOException e) {
		LOG.info("IOException occurred: " + e.getMessage());
	}
	return path;
}
}

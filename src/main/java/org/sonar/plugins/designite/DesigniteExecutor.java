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
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandException;
import org.sonar.api.utils.command.CommandExecutor;

import java.util.concurrent.TimeUnit;

public class DesigniteExecutor 
{
	private static final Logger LOG = LoggerFactory.getLogger(DesigniteExecutor.class);
	
  public void execute(String executable, String solutionPath, String reportFilePath, int timeout) 
  {
    Command cmd = Command.create(executable)
      .addArgument(solutionPath)
      .addArgument("-X")
      .addArgument(reportFilePath);

    int exitCode = CommandExecutor.create().execute(cmd, TimeUnit.MINUTES.toMillis(timeout));

    if (exitCode != 0) 
    {
      throw new CommandException(cmd, "Designite execution failed with exit code: " + exitCode, null);
    }
  }
}

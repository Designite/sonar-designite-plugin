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
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import java.nio.charset.StandardCharsets;

public class DesigniteRulesDefinition implements RulesDefinition 
{
  private static final String DESIGNITE_RULES_XML_PATH = "/org/sonar/plugins/designite/rules.xml";
  private static final Logger LOG = LoggerFactory.getLogger(DesigniteRulesDefinition.class);
  private final RulesDefinitionXmlLoader rulesDefinitionXmlLoader = new RulesDefinitionXmlLoader();

  public DesigniteRulesDefinition(DesigniteConfiguration conf) 
  {
  }

  public void define(Context context) 
  {
	LOG.info("Designite rule definitions import start.");
    NewRepository repository = context
      .createRepository(DesignitePlugin.REPOSITORY_KEY, DesignitePlugin.LANGUAGE_KEY)
      .setName(DesignitePlugin.REPOSITORY_NAME);

    if(repository==null)
    	LOG.info("Designite repository is null");
    if(getClass().getResourceAsStream(DESIGNITE_RULES_XML_PATH)==null)
    	LOG.info("Designite rules file stream is null");
    
    rulesDefinitionXmlLoader.load(repository, getClass().getResourceAsStream(DESIGNITE_RULES_XML_PATH), 
    		StandardCharsets.UTF_8.name());

    repository.done();
    LOG.info("Designite rule definitions are imported.");
  }
}

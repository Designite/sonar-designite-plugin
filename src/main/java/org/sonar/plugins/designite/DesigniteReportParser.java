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

import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DesigniteReportParser 
{
  private final DesigniteReportParserCallback callback;
 
  public DesigniteReportParser(DesigniteReportParserCallback callback) 
  {
    this.callback = callback;
  }

  public void parse(File file) 
  {
    new Parser(callback).parse(file);
  }

  private static class Parser 
  {
    private final DesigniteReportParserCallback callback;

    public Parser(DesigniteReportParserCallback callback) 
    {
      this.callback = callback;
    }

    public void parse(File file) 
    {
      parseForSmell(file, "DesignSmell");
      parseForSmell(file, "ImplementationSmell");
    }

	private void parseForSmell(File file, String smellType) {
		try
		  {
		  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
					
			NodeList nList = doc.getElementsByTagName(smellType);

			for (int counter = 0; counter < nList.getLength(); counter++) 
			{
				String key = null;
				String description = null;
				String filePath = null;
				
				Node nNode = nList.item(counter);
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element eElement = (Element) nNode;

					key = eElement.getAttribute("Key");
					description = eElement.getElementsByTagName("Description").item(0).getTextContent();	
					
					NodeList entityNodes = eElement.getElementsByTagName("Entity");
					for(int i=0; i < entityNodes.getLength(); i++)
					{
						Node entityNode = entityNodes.item(i);
						if(entityNode.getNodeType() == Node.ELEMENT_NODE)
						{
							Element eEntity = (Element)entityNode;
							filePath = eEntity.getElementsByTagName("File").item(0).getTextContent();
						}
					}
				}
				if(key != null && description != null && filePath != null)
				callback.onIssue(key, description, filePath);
			}
		  }
		  catch (IOException e) 
		  {
		      throw Throwables.propagate(e);
		  } 
		  catch (ParserConfigurationException e) 
		  {
		      throw Throwables.propagate(e);
		  }
		  catch (SAXException e) 
		  {
		      throw Throwables.propagate(e);
		  }
	}
  }
}

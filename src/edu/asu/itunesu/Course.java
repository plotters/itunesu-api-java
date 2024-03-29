/*
 * Copyright (c) 2007-2008, Arizona State University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Arizona State University nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ARIZONA STATE UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ARIZONA STATE UNIVERSITY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.asu.itunesu;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A collection of {@link Group} objects.
 */
public class Course extends ITunesUElement implements SectionItem {
    private String name;
    private String handle;
    private String shortName;
    private String identifier;
    private String instructor;
    private String description;
    private List<Permission> permissions;
    private List<Group> groups;
    private Boolean allowSubscription;
    private String themeHandle;

    public Course() {
        this.permissions = new ArrayList<Permission>();
        this.groups = new ArrayList<Group>();
    }

    public Course(String name,
                  String handle,
                  String shortName,
                  String identifier,
                  String instructor,
                  String description,
                  List<Permission> permissions,
                  List<Group> groups,
                  Boolean allowSubscription,
                  String themeHandle) {
        this.name = name;
        this.handle = handle;
        this.shortName = shortName;
        this.identifier = identifier;
        this.instructor = instructor;
        this.description = description;
        this.permissions = permissions;
        this.groups = groups;
        this.allowSubscription = allowSubscription;
        this.themeHandle = themeHandle;
    }

    public String getName() {
        return this.name;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getInstructor() {
        return this.instructor;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public List<Group> getGroups() {
        return this.groups;
    }

    public Boolean getAllowSubscription() {
        return this.allowSubscription;
    }

    public String getThemeHandle() {
        return this.themeHandle;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setAllowSubscription(Boolean allowSubscription) {
        this.allowSubscription = allowSubscription;
    }

    public void setThemeHandle(String themeHandle) {
        this.themeHandle = themeHandle;
    }

    public Element toXmlElement(Document doc) {
        Element element = doc.createElement("Course");
        if (this.name != null) {
            Element nameElement = doc.createElement("Name");
            nameElement.setTextContent(this.name);
            element.appendChild(nameElement);
        }
        if (this.handle != null) {
            Element handleElement = doc.createElement("Handle");
            handleElement.setTextContent(this.handle);
            element.appendChild(handleElement);
        }
        if (this.shortName != null) {
            Element shortNameElement = doc.createElement("ShortName");
            shortNameElement.setTextContent(this.shortName);
            element.appendChild(shortNameElement);
        }
        if (this.identifier != null) {
            Element identifierElement = doc.createElement("Identifier");
            identifierElement.setTextContent(this.identifier);
            element.appendChild(identifierElement);
        }
        if (this.instructor != null) {
            Element instructorElement = doc.createElement("Instructor");
            instructorElement.setTextContent(this.instructor);
            element.appendChild(instructorElement);
        }
        if (this.description != null) {
            Element descriptionElement = doc.createElement("Description");
            descriptionElement.setTextContent(this.description);
            element.appendChild(descriptionElement);
        }
        for (Group group : this.groups) {
            element.appendChild(group.toXmlElement(doc));
        }
        if (this.allowSubscription != null) {
            Element allowSubscriptionElement =
                doc.createElement("AllowSubscription");
            allowSubscriptionElement.setTextContent(this.allowSubscription
                                                    ? "true" : "false");
            element.appendChild(allowSubscriptionElement);
        }
        if (this.themeHandle != null) {
            Element themeHandleElement = doc.createElement("ThemeHandle");
            themeHandleElement.setTextContent(this.themeHandle);
            element.appendChild(themeHandleElement);
        }
        for (Permission permission : this.permissions) {
            element.appendChild(permission.toXmlElement(doc));
        }
        return element;
    }

    public static Course fromXmlElement(Element element) throws ITunesUException {
        if (!"Course".equals(element.getNodeName())) {
            throw new ITunesUException("Expected Course, got "
                                       + element.getNodeName());
        }
        String name = null;
        String handle = null;
        String shortName = null;
        String identifier = null;
        String instructor = null;
        String description = null;
        List<Permission> permissions = new ArrayList<Permission>();
        List<Group> groups = new ArrayList<Group>();
        Boolean allowSubscription = null;
        String themeHandle = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if ("Name".equals(childNode.getNodeName())) {
                    name = childNode.getTextContent();
                } else if ("Handle".equals(childNode.getNodeName())) {
                    handle = childNode.getTextContent();
                } else if ("ShortName".equals(childNode.getNodeName())) {
                    shortName = childNode.getTextContent();
                } else if ("Identifier".equals(childNode.getNodeName())) {
                    identifier = childNode.getTextContent();
                } else if ("Instructor".equals(childNode.getNodeName())) {
                    instructor = childNode.getTextContent();
                } else if ("Description".equals(childNode.getNodeName())) {
                    description = childNode.getTextContent();
                } else if ("Permission".equals(childNode.getNodeName())) {
                    permissions.add(Permission.fromXmlElement((Element) childNode));
                } else if ("Group".equals(childNode.getNodeName())) {
                    groups.add(Group.fromXmlElement((Element) childNode));
                } else if ("AllowSubscription".equals(childNode.getNodeName())) {
                    allowSubscription = "true".equals(childNode.getTextContent());
                } else if ("ThemeHandle".equals(childNode.getNodeName())) {
                    themeHandle = childNode.getTextContent();
                }
            }
        }
        return new Course(name,
                          handle,
                          shortName,
                          identifier,
                          instructor,
                          description,
                          permissions,
                          groups,
                          allowSubscription,
                          themeHandle);
    }

    public static Course fromXml(String xml)
        throws ITunesUException {
        DocumentBuilderFactory docFactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ITunesUException(e);
        }
        Document doc;
        try {
            doc = docBuilder.parse(new InputSource(new StringReader(xml)));
        } catch (SAXException e) {
            throw new ITunesUException(e);
        } catch (IOException e) {
            throw new ITunesUException(e);
        }
        return Course.fromXmlElement(doc.getDocumentElement());
    }

    public String toString() {
        return (super.toString()
                + "[name="
                + (this.getName() == null ? "<null>" : this.getName())
                + ",handle="
                + (this.getHandle() == null ? "<null>" : this.getHandle())
                + "]");
    }
}

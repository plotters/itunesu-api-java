/*
 * Copyright (c) 2007, Arizona State University
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
 * A collection of {@link Section} objects.
 */
public class Division implements SectionItem {
    private String name;
    private String handle;
    private String shortName;
    private String identifier;
    private Boolean allowSubscription;
    private List<Permission> permissions;
    private List<Section> sections;

    public Division() {
        this.permissions = new ArrayList<Permission>();
        this.sections = new ArrayList<Section>();
    }

    public Division(String name,
                    String handle,
                    String shortName,
                    String identifier,
                    Boolean allowSubscription,
                    List<Permission> permissions,
                    List<Section> sections) {
        this.name = name;
        this.handle = handle;
        this.shortName = shortName;
        this.identifier = identifier;
        this.allowSubscription = allowSubscription;
        this.permissions = permissions;
        this.sections = sections;
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

    public Boolean getAllowSubscription() {
        return this.allowSubscription;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public List<Section> getSections() {
        return this.sections;
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

    public void setAllowSubscription(Boolean allowSubscription) {
        this.allowSubscription = allowSubscription;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Element toXmlElement(Document doc) {
        Element element = doc.createElement("Division");
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
        if (this.allowSubscription != null) {
            Element allowSubscriptionElement =
                doc.createElement("AllowSubscription");
            allowSubscriptionElement.setTextContent(this.allowSubscription
                                                    ? "true" : "false");
            element.appendChild(allowSubscriptionElement);
        }
        for (Permission permission : this.permissions) {
            element.appendChild(permission.toXmlElement(doc));
        }
        for (Section section : this.sections) {
            element.appendChild(section.toXmlElement(doc));
        }
        return element;
    }

    public static Division fromXmlElement(Element element) throws ITunesUException {
        if (!"Division".equals(element.getNodeName())) {
            throw new ITunesUException("Expected Division, got "
                                       + element.getNodeName());
        }
        String name = null;
        String handle = null;
        String shortName = null;
        String identifier = null;
        Boolean allowSubscription = false;
        List<Permission> permissions = new ArrayList<Permission>();
        List<Section> sections = new ArrayList<Section>();
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
                } else if ("AllowSubscription".equals(childNode.getNodeName())) {
                    allowSubscription = "true".equals(childNode.getTextContent());
                } else if ("Permission".equals(childNode.getNodeName())) {
                    permissions.add(Permission.fromXmlElement((Element) childNode));
                } else if ("Section".equals(childNode.getNodeName())) {
                    sections.add(Section.fromXmlElement((Element) childNode));
                }
            }
        }
        return new Division(name,
                            handle,
                            shortName,
                            identifier,
                            allowSubscription,
                            permissions,
                            sections);
    }

    public static Division fromXml(String xml)
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
        return Division.fromXmlElement(doc.getDocumentElement());
    }

    public String toString() {
        return (super.toString()
                + "[name="
                + (this.getName() == null ? "<null>" : this.getName())
                + ",handle="
                + (this.getHandle() == null ? "<handle>" : this.getHandle())
                + "]");
    }
}

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

import java.io.StringWriter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ITunesUDocument {
    public static final String VERSION = "1.0.2";

    private String method;
    private Map<String, Object> arguments;

    public ITunesUDocument(String method, Map<String, Object> arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    public String getMethod() {
        return this.method;
    }

    public Map<String, Object> getArguments() {
        return this.arguments;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public Document toXmlDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("ITunesUDocument");
        doc.appendChild(root);

        Element version = doc.createElement("Version");
        version.setTextContent(VERSION);
        root.appendChild(version);

        Element method = doc.createElement(this.method);
        root.appendChild(method);

        for (String name : this.arguments.keySet()) {
            Object value = this.arguments.get(name);
            if (value instanceof String) {
                Element argument = doc.createElement(name);
                argument.setTextContent((String) value);
                method.appendChild(argument);
            } else if (value instanceof List) {
                List elementList = (List) value;
                for (Object object : elementList) {
                    Element element = ((ITunesUElement) object).toXmlElement(doc);
                    method.appendChild(element);
                }
            } else {
                Element element = ((ITunesUElement) value).toXmlElement(doc);
                method.appendChild(element);
            }
        }

        return doc;
    }

    public String toXml()
        throws ParserConfigurationException,
               TransformerException {

        Document doc = this.toXmlDocument();

        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer trans = transFactory.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);

        return writer.toString();
    }

    public static ITunesUDocument buildShowTree(String handle, String keyGroup) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("Handle", handle);
        arguments.put("KeyGroup", keyGroup);
        ITunesUDocument doc = new ITunesUDocument("ShowTree", arguments);
        return doc;
    }

    public static ITunesUDocument buildMergeSite(String siteHandle, Site site, boolean mergeByHandle, boolean destructive) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        if (siteHandle != null) {
            arguments.put("SiteHandle", siteHandle);
        }
        arguments.put("MergeByHandle", mergeByHandle ? "true" : "false");
        arguments.put("Destructive", destructive ? "true" : "false");
        arguments.put("Site", site);
        ITunesUDocument doc = new ITunesUDocument("MergeSite", arguments);
        return doc;
    }

    public static ITunesUDocument buildAddDivision(String parentHandle, String templateHandle, Division division) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        if (templateHandle != null) {
            arguments.put("TemplateHandle", templateHandle);
        }
        arguments.put("Division", division);
        ITunesUDocument doc = new ITunesUDocument("AddDivision", arguments);
        return doc;
    }

    public static ITunesUDocument buildDeleteDivision(String divisionHandle) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("DivisionHandle", divisionHandle);
        arguments.put("DivisionPath", "");
        ITunesUDocument doc = new ITunesUDocument("DeleteDivision", arguments);
        return doc;
    }

    public static ITunesUDocument buildMergeDivision(String divisionHandle, Division division, boolean mergeByHandle, boolean destructive) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("DivisionHandle", divisionHandle);
        arguments.put("DivisionPath", "");
        arguments.put("MergeByHandle", mergeByHandle ? "true" : "false");
        arguments.put("Destructive", destructive ? "true" : "false");
        arguments.put("Division", division);
        ITunesUDocument doc = new ITunesUDocument("MergeDivision", arguments);
        return doc;
    }

    public static ITunesUDocument buildAddSection(String parentHandle, Section section) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        arguments.put("Section", section);
        ITunesUDocument doc = new ITunesUDocument("AddSection", arguments);
        return doc;
    }

    public static ITunesUDocument buildDeleteSection(String sectionHandle) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("SectionHandle", sectionHandle);
        arguments.put("SectionPath", "");
        ITunesUDocument doc = new ITunesUDocument("DeleteSection", arguments);
        return doc;
    }

    public static ITunesUDocument buildMergeSection(String sectionHandle, Section section, boolean mergeByHandle, boolean destructive) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("SectionHandle", sectionHandle);
        arguments.put("SectionPath", "");
        arguments.put("MergeByHandle", mergeByHandle ? "true" : "false");
        arguments.put("Destructive", destructive ? "true" : "false");
        arguments.put("Section", section);
        ITunesUDocument doc = new ITunesUDocument("MergeSection", arguments);
        return doc;
    }

    public static ITunesUDocument buildAddCourse(String parentHandle, String templateHandle, Course course) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        if (templateHandle != null) {
            arguments.put("TemplateHandle", templateHandle);
        }
        arguments.put("Course", course);
        ITunesUDocument doc = new ITunesUDocument("AddCourse", arguments);
        return doc;
    }

    public static ITunesUDocument buildDeleteCourse(String courseHandle) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("CourseHandle", courseHandle);
        arguments.put("CoursePath", "");
        ITunesUDocument doc = new ITunesUDocument("DeleteCourse", arguments);
        return doc;
    }

    public static ITunesUDocument buildMergeCourse(String courseHandle, Course course, boolean mergeByHandle, boolean destructive) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("CourseHandle", courseHandle);
        arguments.put("CoursePath", "");
        arguments.put("MergeByHandle", mergeByHandle ? "true" : "false");
        arguments.put("Destructive", destructive ? "true" : "false");
        arguments.put("Course", course);
        ITunesUDocument doc = new ITunesUDocument("MergeCourse", arguments);
        return doc;
    }

    public static ITunesUDocument buildAddGroup(String parentHandle, Group group) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        arguments.put("Group", group);
        ITunesUDocument doc = new ITunesUDocument("AddGroup", arguments);
        return doc;
    }

    public static ITunesUDocument buildDeleteGroup(String groupHandle) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("GroupHandle", groupHandle);
        arguments.put("GroupPath", "");
        ITunesUDocument doc = new ITunesUDocument("DeleteGroup", arguments);
        return doc;
    }

    public static ITunesUDocument buildMergeGroup(String groupHandle, Group group, boolean mergeByHandle, boolean destructive) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("GroupHandle", groupHandle);
        arguments.put("GroupPath", "");
        arguments.put("MergeByHandle", mergeByHandle ? "true" : "false");
        arguments.put("Destructive", destructive ? "true" : "false");
        arguments.put("Group", group);
        ITunesUDocument doc = new ITunesUDocument("MergeGroup", arguments);
        return doc;
    }

    public static ITunesUDocument buildAddTrack(String parentHandle, Track track) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        arguments.put("Track", track);
        ITunesUDocument doc = new ITunesUDocument("AddTrack", arguments);
        return doc;
    }

    public static ITunesUDocument buildDeleteTrack(String trackHandle) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("TrackHandle", trackHandle);
        ITunesUDocument doc = new ITunesUDocument("DeleteTrack", arguments);
        return doc;
    }

    public static ITunesUDocument buildMergeTrack(String trackHandle, Track track) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("TrackHandle", trackHandle);
        arguments.put("Track", track);
        ITunesUDocument doc = new ITunesUDocument("MergeTrack", arguments);
        return doc;
    }

    public static ITunesUDocument buildAddPermission(String parentHandle, Permission permission) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        arguments.put("Permission", permission);
        ITunesUDocument doc = new ITunesUDocument("AddPermission", arguments);
        return doc;
    }

    public static ITunesUDocument buildDeletePermission(String parentHandle, String credential) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        arguments.put("Credential", credential);
        ITunesUDocument doc = new ITunesUDocument("DeletePermission", arguments);
        return doc;
    }

    public static ITunesUDocument buildMergePermission(String parentHandle, Permission permission) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("ParentHandle", parentHandle);
        arguments.put("ParentPath", "");
        arguments.put("Permission", permission);
        ITunesUDocument doc = new ITunesUDocument("MergePermission", arguments);
        return doc;
    }

    public static ITunesUDocument buildAddCredential(String credential) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("Credential", credential);
        ITunesUDocument doc = new ITunesUDocument("AddCredential", arguments);
        return doc;
    }

    public static ITunesUDocument buildDeleteCredential(String credential) {
        Map<String, Object> arguments = new LinkedHashMap<String, Object>();
        arguments.put("Credential", credential);
        ITunesUDocument doc = new ITunesUDocument("DeleteCredential", arguments);
        return doc;
    }
}

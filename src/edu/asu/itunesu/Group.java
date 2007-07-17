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
 * A collection of {@link Track} objects, viewable as a tab.
 */
public class Group implements ITunesUElement {
    private String name;
    private String handle;
    private List<Permission> permissions;
    private List<Track> tracks;

    public Group() {
        this.permissions = new ArrayList<Permission>();
        this.tracks = new ArrayList<Track>();
    }

    public Group(String name,
                 String handle,
                 List<Permission> permissions,
                 List<Track> tracks) {
        this.name = name;
        this.handle = handle;
        this.permissions = permissions;
        this.tracks = tracks;
    }

    public String getName() {
        return this.name;
    }

    public String getHandle() {
        return this.handle;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Element toXmlElement(Document doc) {
        Element element = doc.createElement("Group");
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
        for (Permission permission : this.permissions) {
            element.appendChild(permission.toXmlElement(doc));
        }
        for (Track track : this.tracks) {
            element.appendChild(track.toXmlElement(doc));
        }
        return element;
    }

    public static Group fromXmlElement(Element element) throws ITunesUException {
        if (!"Group".equals(element.getNodeName())) {
            throw new ITunesUException("Expected Group, got "
                                       + element.getNodeName());
        }
        String name = null;
        String handle = null;
        List<Permission> permissions = new ArrayList<Permission>();
        List<Track> tracks = new ArrayList<Track>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if ("Name".equals(childNode.getNodeName())) {
                    name = childNode.getTextContent();
                } else if ("Handle".equals(childNode.getNodeName())) {
                    handle = childNode.getTextContent();
                } else if ("Permission".equals(childNode.getNodeName())) {
                    permissions.add(Permission.fromXmlElement((Element) childNode));
                } else if ("Track".equals(childNode.getNodeName())) {
                    tracks.add(Track.fromXmlElement((Element) childNode));
                }
            }
        }
        return new Group(name, handle, permissions, tracks);
    }

    public static Group fromXml(String xml)
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
        return Group.fromXmlElement(doc.getDocumentElement());
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

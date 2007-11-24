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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A permission record containing credential and access settings.
 */
public class Permission implements ITunesUElement {
    public static final String ACCESS_NO_ACCESS = "No Access";
    public static final String ACCESS_DOWNLOAD  = "Download";
    public static final String ACCESS_DROP_BOX  = "Drop Box";
    public static final String ACCESS_SHARED    = "Shared";
    public static final String ACCESS_EDIT      = "Edit";

    private String credential;
    private String access;

    public Permission() {}

    public Permission(String credential, String access) {
        this.credential = credential;
        this.access = access;
    }

    public String getCredential() {
        return this.credential;
    }

    public String getAccess() {
        return this.access;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public Element toXmlElement(Document doc) {
        Element element = doc.createElement("Permission");
        if (this.credential != null) {
            Element credentialElement = doc.createElement("Credential");
            credentialElement.setTextContent(this.credential);
            element.appendChild(credentialElement);
        }
        if (this.access != null) {
            Element accessElement = doc.createElement("Access");
            accessElement.setTextContent(this.access);
            element.appendChild(accessElement);
        }
        return element;
    }

    public static Permission fromXmlElement(Element element) throws ITunesUException {
        if (!"Permission".equals(element.getNodeName())) {
            throw new ITunesUException("Expected Permission, got "
                                       + element.getNodeName());
        }
        String credential = null;
        String access = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if ("Credential".equals(childNode.getNodeName())) {
                    credential = childNode.getTextContent();
                } else if ("Access".equals(childNode.getNodeName())) {
                    access = childNode.getTextContent();
                }
            }
        }
        return new Permission(credential, access);
    }

    public String toString() {
        return (super.toString()
                + "[credential="
                + (this.getCredential() == null ? "<null>" : this.getCredential())
                + ",access="
                + (this.getAccess() == null ? "<null>" : this.getAccess())
                + "]");
    }
}

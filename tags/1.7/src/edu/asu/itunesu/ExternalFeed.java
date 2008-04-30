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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An external RSS feed for a {@link Group}.
 */
public class ExternalFeed extends ITunesUElement {
    public static final String POLLING_INTERVAL_NEVER = "Never";
    public static final String POLLING_INTERVAL_DAILY = "Daily";

    public static final String SECURITY_TYPE_NONE     = "None";
    public static final String SECURITY_TYPE_BASIC    = "HTTP Basic Authentication";

    public static final String SIGNATURE_TYPE_NONE    = "None";
    public static final String SIGNATURE_TYPE_SHA256  = "Append SHA-256";

    private String url;
    private String ownerEmail;
    private String pollingInterval;
    private String securityType;
    private String signatureType;
    private String basicAuthUsername;
    private String basicAuthPassword;
    private String status;

    public ExternalFeed() {}

    public ExternalFeed(String url,
                        String ownerEmail,
                        String pollingInterval,
                        String securityType,
                        String signatureType,
                        String basicAuthUsername,
                        String basicAuthPassword,
                        String status) {
        this.url = url;
        this.ownerEmail = ownerEmail;
        this.pollingInterval = pollingInterval;
        this.securityType = securityType;
        this.signatureType = signatureType;
        this.basicAuthUsername = basicAuthUsername;
        this.basicAuthPassword = basicAuthPassword;
        this.status = status;
    }

    public String getUrl() {
        return this.url;
    }

    public String getOwnerEmail() {
        return this.ownerEmail;
    }

    public String getPollingInterval() {
        return this.pollingInterval;
    }

    public String getSecurityType() {
        return this.securityType;
    }

    public String getSignatureType() {
        return this.signatureType;
    }

    public String getBasicAuthUsername() {
        return this.basicAuthUsername;
    }

    public String getBasicAuthPassword() {
        return this.basicAuthPassword;
    }

    public String getStatus() {
        return this.status;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setPollingInterval(String pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
    }

    public void setBasicAuthUsername(String basicAuthUsername) {
        this.basicAuthUsername = basicAuthUsername;
    }

    public void setBasicAuthPassword(String basicAuthPassword) {
        this.basicAuthPassword = basicAuthPassword;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Element toXmlElement(Document doc) {
        Element element = doc.createElement("ExternalFeed");
        if (this.url != null) {
            Element urlElement = doc.createElement("URL");
            urlElement.setTextContent(this.url);
            element.appendChild(urlElement);
        }
        if (this.ownerEmail != null) {
            Element ownerEmailElement = doc.createElement("OwnerEmail");
            ownerEmailElement.setTextContent(this.ownerEmail);
            element.appendChild(ownerEmailElement);
        }
        if (this.pollingInterval != null) {
            Element pollingIntervalElement = doc.createElement("PollingInterval");
            pollingIntervalElement.setTextContent(this.pollingInterval);
            element.appendChild(pollingIntervalElement);
        }
        if (this.securityType != null) {
            Element securityTypeElement = doc.createElement("SecurityType");
            securityTypeElement.setTextContent(this.securityType);
            element.appendChild(securityTypeElement);
        }
        if (this.signatureType != null) {
            Element signatureTypeElement = doc.createElement("SignatureType");
            signatureTypeElement.setTextContent(this.signatureType);
            element.appendChild(signatureTypeElement);
        }
        if (this.basicAuthUsername != null) {
            Element basicAuthUsernameElement = doc.createElement("BasicAuthUsername");
            basicAuthUsernameElement.setTextContent(this.basicAuthUsername);
            element.appendChild(basicAuthUsernameElement);
        }
        if (this.basicAuthPassword != null) {
            Element basicAuthPasswordElement = doc.createElement("BasicAuthPassword");
            basicAuthPasswordElement.setTextContent(this.basicAuthPassword);
            element.appendChild(basicAuthPasswordElement);
        }
        if (this.status != null) {
            Element statusElement = doc.createElement("Status");
            statusElement.setTextContent(this.status);
            element.appendChild(statusElement);
        }
        return element;
    }

    public static ExternalFeed fromXmlElement(Element element) throws ITunesUException {
        if (!"ExternalFeed".equals(element.getNodeName())) {
            throw new ITunesUException("Expected ExternalFeed, got "
                                       + element.getNodeName());
        }
        String url = null;
        String ownerEmail = null;
        String pollingInterval = null;
        String securityType = null;
        String signatureType = null;
        String basicAuthUsername = null;
        String basicAuthPassword = null;
        String status = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if ("URL".equals(childNode.getNodeName())) {
                    url = childNode.getTextContent();
                } else if ("OwnerEmail".equals(childNode.getNodeName())) {
                    ownerEmail = childNode.getTextContent();
                } else if ("PollingInterval".equals(childNode.getNodeName())) {
                    pollingInterval = childNode.getTextContent();
                } else if ("SecurityType".equals(childNode.getNodeName())) {
                    securityType = childNode.getTextContent();
                } else if ("SignatureType".equals(childNode.getNodeName())) {
                    signatureType = childNode.getTextContent();
                } else if ("BasicAuthUsername".equals(childNode.getNodeName())) {
                    basicAuthUsername = childNode.getTextContent();
                } else if ("BasicAuthPassword".equals(childNode.getNodeName())) {
                    basicAuthPassword = childNode.getTextContent();
                } else if ("Status".equals(childNode.getNodeName())) {
                    status = childNode.getTextContent();
                }
            }
        }
        return new ExternalFeed(url,
                                ownerEmail,
                                pollingInterval,
                                securityType,
                                signatureType,
                                basicAuthUsername,
                                basicAuthPassword,
                                status);
    }

    public String toString() {
        return (super.toString()
                + "[url="
                + (this.getUrl() == null ? "<null>" : this.getUrl())
                + "]");
    }
}

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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class ITunesUResponse {
    private String version;
    private String error;
    private String addedObjectHandle;
    private String resultXml;

    public ITunesUResponse() {}

    public ITunesUResponse(String version,
                           String error,
                           String addedObjectHandle,
                           String resultXml) {
        this.version = version;
        this.error = error;
        this.addedObjectHandle = addedObjectHandle;
        this.resultXml = resultXml;
    }

    public String getVersion() {
        return this.version;
    }

    public String getError() {
        return this.error;
    }

    public String getAddedObjectHandle() {
        return this.addedObjectHandle;
    }

    public String getResultXml() {
        return this.resultXml;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setAddedObjectHandle(String addedObjectHandle) {
        this.addedObjectHandle = addedObjectHandle;
    }

    public void setResultXml(String resultXml) {
        this.resultXml = resultXml;
    }

    public static ITunesUResponse fromXmlElement(Element element) throws ITunesUException {
        if (!"ITunesUResponse".equals(element.getNodeName())) {
            throw new ITunesUException("Expected ITunesUResponse, got "
                                       + element.getNodeName());
        }
        String version = null;
        String error = null;
        String addedObjectHandle = null;
        String resultXml = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if ("Version".equals(childNode.getNodeName())) {
                    version = childNode.getTextContent();
                } else if ("error".equals(childNode.getNodeName())) {
                    error = childNode.getTextContent();
                } else if ("AddedObjectHandle".equals(childNode.getNodeName())) {
                    addedObjectHandle = childNode.getTextContent();
                } else {
                    try {
                        Element siteElement = (Element) childNode;

                        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                        Document doc = docBuilder.newDocument();
                        doc.appendChild(doc.importNode(siteElement, true));

                        TransformerFactory transFactory = TransformerFactory.newInstance();
                        Transformer trans = transFactory.newTransformer();
                        trans.setOutputProperty(OutputKeys.INDENT, "yes");

                        StringWriter writer = new StringWriter();
                        StreamResult result = new StreamResult(writer);
                        DOMSource source = new DOMSource(doc);
                        trans.transform(source, result);

                        resultXml = writer.toString();
                    } catch (TransformerException e) {
                        throw new ITunesUException(e);
                    } catch (ParserConfigurationException e) {
                        throw new ITunesUException(e);
                    }
                }
            }
        }
        return new ITunesUResponse(version, error, addedObjectHandle, resultXml);
    }

    public static ITunesUResponse fromXml(String xml) throws ITunesUException {
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
        return ITunesUResponse.fromXmlElement(doc.getDocumentElement());
    }
}

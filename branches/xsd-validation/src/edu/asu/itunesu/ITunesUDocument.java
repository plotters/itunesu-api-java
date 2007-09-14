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

class ITunesUDocument {
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

    public String toXml() throws ParserConfigurationException, TransformerException {
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
}

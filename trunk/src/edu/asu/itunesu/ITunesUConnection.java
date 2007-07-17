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
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The iTunesU Web Services API connection.
 * 
 * @author <a href="mailto:ramen@asu.edu">Dave Benjamin</a>
 */
public class ITunesUConnection {
    private String siteUrl;
    private String debugSuffix;
    private String sharedSecret;
    private String[] credentials;

    private String identity;

    private boolean debug;

    /**
     * Constructor.
     *  
     * @param siteUrl      The URL to your site in iTunes U. The last
     *                     component of that URL, after the last slash,
     *                     is a domain name that uniquely identifies your
     *                     site within iTunes U.
     * @param debugSuffix  A suffix you can append to your site URL
     *                     to obtain debugging information about the
     *                     transmission of credentials and identity
     *                     information from your institution's
     *                     authentication and authorization services
     *                     to iTunes U.
     * @param sharedSecret A secret key known only to you and Apple that
     *                     allows you to control who has access to your
     *                     site and what access they have to it.
     * @param credentials  The credential strings to assign
     *                     to users who should have the
     *                     permission to administer your
     *                     iTunes U site.
     */
    public ITunesUConnection(String siteUrl,
                             String debugSuffix,
                             String sharedSecret,
                             String[] credentials) {
        this.siteUrl = siteUrl;
        this.debugSuffix = debugSuffix;
        this.sharedSecret = sharedSecret;
        this.credentials = credentials;
        this.debug = false;
    }

    /**
     * Sets the identity from user information.
     * Calling this method is optional. All parameters may be null.
     * 
     * @param displayName The user's name.
     * @param emailAddress The user's email address.
     * @param username The user's username.
     * @param userIdentifier A unique identifier for the user.
     */
    public void setIdentity(String displayName,
                            String emailAddress,
                            String username,
                            String userIdentifier) throws ITunesUException {
    	ITunesU iTunesU = new ITunesU();
        this.identity = iTunesU.getIdentityString(displayName,
                                                  emailAddress,
                                                  username,
                                                  userIdentifier);
    }

    /**
     * Sets the identity from a string.
     */
    public void setIdentity(String identity) {
    	this.identity = identity;
    }
    
    /**
     * Gets the value of the debug flag which determines whether or not to
     * use the debug prefix in requests. This flag is false by default.
     * 
     * @return True if the debug prefix is enabled.
     */
    public boolean getDebug() {
        return this.debug;
    }

    /**
     * Sets the value of the debug flag which determines whether or not to
     * use the debug prefix in requests.
     * 
     * @param debug True to enable the debug prefix, false otherwise.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Retrieves the entire site.
     * 
     * @return A {@link Site} model object.
     */
    public Site getSite() throws ITunesUException {
        return Site.fromXml(this.showTree(null));
    }

    /**
     * Retrieves the entire site, loading a minimal amount of data.
     * Permissions and tracks are omitted. Only the name and handle
     * attributes are populated.
     * 
     * @return A {@link Site} model object.
     */
    public Site getSiteMinimal() throws ITunesUException {
        return Site.fromXml(this.showTree(null, "minimal"));
    }

    /**
     * Retrieves a section by its handle.
     * 
     * @param handle The handle of the section.
     * @return A {@link Section} model object.
     */
    public Section getSection(String handle) throws ITunesUException {
        return Section.fromXml(this.showTree(handle));
    }

    /**
     * Retrieves a division by its handle.
     * 
     * @param handle The handle of the division.
     * @return A {@link Division} model object.
     */
    public Division getDivision(String handle) throws ITunesUException {
        return Division.fromXml(this.showTree(handle));
    }

    /**
     * Retrieves a course by its handle.
     * 
     * @param handle The handle of the course.
     * @return A {@link Course} model object.
     */
    public Course getCourse(String handle) throws ITunesUException {
        return Course.fromXml(this.showTree(handle));
    }

    /**
     * Retrieves a group by its handle.
     * 
     * @param handle The handle of the group.
     * @return A {@link Group} model object.
     */
    public Group getGroup(String handle) throws ITunesUException {
        return Group.fromXml(this.showTree(handle));
    }

    /**
     * Adds a course to a section.
     * 
     * @param parentHandle Handle for the parent section.
     * @param templateHandle Handle for the course template, or null if none.
     * @param course Object containing course information.
     */
    public void addCourse(String parentHandle,
                          String templateHandle,
                          Course course)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        if (parentHandle != null) {
            arguments.put("ParentHandle", parentHandle);
        }

        if (templateHandle != null) {
            arguments.put("TemplateHandle", templateHandle);
        }

        arguments.put("Course", course);
        
        ITunesUDocument doc = new ITunesUDocument("AddCourse", arguments);
        this.send(null, doc);
    }

    /**
     * Deletes a course from a section.
     * 
     * @param courseHandle Handle for the course to delete.
     */
    public void deleteCourse(String courseHandle)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        if (courseHandle != null) {
            arguments.put("CourseHandle", courseHandle);
        }

        ITunesUDocument doc = new ITunesUDocument("DeleteCourse", arguments);
        this.send(null, doc);
    }

    /**
     * Updates course information. Same as calling mergeCourse() with
     * mergeByHandle and destructive set to false.
     * 
     * @param courseHandle Handle for the course to update.
     * @param course Object containing course information.
     */
    public void mergeCourse(String courseHandle, Course course)
        throws ITunesUException {

        this.mergeCourse(courseHandle, course, false, false);
    }

    /**
     * Updates course information. Same as calling mergeCourse() with
     * destructive set to false.
     * 
     * @param courseHandle Handle for the course to update.
     * @param course Object containing course information.
     * @param mergeByHandle If true, merge groups by handle.
     *                      Otherwise, merge by name.
     */
    public void mergeCourse(String courseHandle,
                            Course course,
                            boolean mergeByHandle)
        throws ITunesUException {

        this.mergeCourse(courseHandle, course, mergeByHandle, false);
    }

    /**
     * Updates course information.
     * 
     * @param courseHandle Handle for the course to update.
     * @param course Object containing course information.
     * @param mergeByHandle If true, merge groups by handle.
     *                      Otherwise, merge by name.
     * @param destructive If true, delete any unspecified
     *                    groups or permissions.
     */
    public void mergeCourse(String courseHandle,
                            Course course,
                            boolean mergeByHandle,
                            boolean destructive)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        if (courseHandle != null) {
            arguments.put("CourseHandle", courseHandle);
        }

        arguments.put("Course", course);
        arguments.put("MergeByHandle", mergeByHandle ? "true" : "false");
        arguments.put("Destructive", destructive ? "true" : "false");

        ITunesUDocument doc = new ITunesUDocument("MergeCourse", arguments);
        this.send(null, doc);
    }

    /**
     * Adds a group (also called a tab) to a course.
     * 
     * @param parentHandle Handle for the parent course.
     * @param group Object containing group information.
     */
    public void addGroup(String parentHandle, Group group)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("ParentHandle", parentHandle);
        arguments.put("Group", group);

        ITunesUDocument doc = new ITunesUDocument("AddGroup", arguments);
        this.send(null, doc);
    }

    /**
     * Deletes a group from a course.
     * 
     * @param groupHandle Handle for the group to delete.
     */
    public void deleteGroup(String groupHandle)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("GroupHandle", groupHandle);

        ITunesUDocument doc = new ITunesUDocument("DeleteGroup", arguments);
        this.send(null, doc);
    }

    /**
     * Updates group information. Same as calling mergeGroup() with
     * destructive set to false.
     * 
     * @param groupHandle Handle for the group to update.
     * @param group Object containing group information.
     */
    public void mergeGroup(String groupHandle, Group group)
        throws ITunesUException {

        this.mergeGroup(groupHandle, group, false);
    }

    /**
     * Updates group information.
     * 
     * @param groupHandle Handle for the group to update.
     * @param group Object containing group information
     * @param destructive If true, delete any unspecified
     *                    tracks or permissions.
     */
    public void mergeGroup(String groupHandle,
                           Group group,
                           boolean destructive)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("GroupHandle", groupHandle);
        arguments.put("Group", group);
        arguments.put("Destructive", destructive ? "true" : "false");

        ITunesUDocument doc = new ITunesUDocument("DeleteGroup", arguments);
        this.send(null, doc);
    }

    /**
     * Adds a permission to a section, course, or group.
     * 
     * @param parentHandle Handle for the parent section, course, or group.
     * @param permission Object containing permission information.
     */
    public void addPermission(String parentHandle,
                              Permission permission)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("ParentHandle", parentHandle);
        arguments.put("Permission", permission);

        ITunesUDocument doc = new ITunesUDocument("AddPermission", arguments);
        this.send(null, doc);
    }

    /**
     * Deletes a permission from a section, course, or group.
     * 
     * @param parentHandle Handle for the parent section, course, or group.
     * @param credential Credential of permission to delete.
     */
    public void deletePermission(String parentHandle,
                                 String credential)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("ParentHandle", parentHandle);
        arguments.put("Credential", credential);

        ITunesUDocument doc =
            new ITunesUDocument("DeletePermission", arguments);
        this.send(null, doc);
    }

    /**
     * Updates permission information.
     * 
     * @param parentHandle Handle for the parent section, course, or group.
     * @param permission Object containing permission information.
     */
    public void mergePermission(String parentHandle,
                                Permission permission)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("ParentHandle", parentHandle);
        arguments.put("Permission", permission);

        ITunesUDocument doc =
            new ITunesUDocument("MergePermission", arguments);
        this.send(null, doc);
    }

    /**
     * Deletes a track.
     * 
     * @param trackHandle Handle for the track to delete.
     */
    public void deleteTrack(String trackHandle)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("TrackHandle", trackHandle);

        ITunesUDocument doc = new ITunesUDocument("DeleteTrack", arguments);
        this.send(null, doc);
    }

    /**
     * Updates track information.
     * 
     * @param trackHandle Handle for the track to update.
     * @param track Object containing track information.
     */
    public void mergeTrack(String trackHandle, Track track)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();
    
        arguments.put("TrackHandle", trackHandle);
        arguments.put("Track", track);
    
        ITunesUDocument doc = new ITunesUDocument("MergeTrack", arguments);
        this.send(null, doc);
    }

    /**
     * Reads XML for an element by its handle.
     * 
     * @param handle Handle of the parent element, or null for the whole site.
     * @return An XML string.
     */
    public String showTree(String handle) throws ITunesUException {
        String prefix = this.getPrefix();
        String url = prefix + "/API/ShowTree/" + this.getDestination(handle);

        ITunesU iTunesU = new ITunesU();

        try {
            return iTunesU.invokeAction(url, this.generateToken());
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }
    }

    /**
     * Reads XML for an element by its handle, specifying a key group.
     * 
     * @param handle Handle of the parent element, or null for the whole site.
     * @param keyGroup Must be one of: minimal, most, maximal
     * @return An XML string.
     */
    public String showTree(String handle, String keyGroup)
        throws ITunesUException {

        Map<String, Object> arguments = new HashMap<String, Object>();

        if (handle != null) {
            arguments.put("Handle", handle);
        }

        arguments.put("KeyGroup", keyGroup);

        ITunesUDocument doc = new ITunesUDocument("ShowTree", arguments);

        try {
            return this.execute(null, doc.toXml());
        } catch (ParserConfigurationException e) {
            throw new ITunesUException(e);
        } catch (TransformerException e) {
            throw new ITunesUException(e);
        }
    }
    
    /**
     * Generates and returns a new iTunesU upload URL.
     * @param handle Handle for the destination.
     * @param forXml True for uploading XML, false for uploading content.
     * @return The URL as a string.
     */
    public String getUploadUrl(String handle, boolean forXml)
        throws ITunesUException {

        ITunesU iTunesU = new ITunesU();

        String url = (this.getPrefix()
                      + "/API/GetUploadURL/"
                      + this.getDestination(handle));

        if (forXml) {
            url += "?type=XMLControlFile";
        }
        
        try {
            return iTunesU.invokeAction(url, this.generateToken());
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }
    }

    private void send(String handle, ITunesUDocument doc)
        throws ITunesUException {
    
        String result;
        
        try {
            result = this.execute(handle, doc.toXml());
        } catch (ParserConfigurationException e) {
            throw new ITunesUException(e);
        } catch (TransformerException e) {
            throw new ITunesUException(e);
        }

        Pattern pattern = Pattern.compile(".*<error>(.*)</error>.*");
        Matcher matcher = pattern.matcher(result);
        if (matcher.matches()) {
            throw new ITunesUException(matcher.group(1));
        }
    }

    @SuppressWarnings("unused")
    private Document sendAndReceive(String handle, ITunesUDocument doc)
        throws ITunesUException {
    
        String result;
        
        try {
            result = this.execute(handle, doc.toXml());
        } catch (ParserConfigurationException e) {
            throw new ITunesUException(e);
        } catch (TransformerException e) {
            throw new ITunesUException(e);
        }
    
        DocumentBuilderFactory docFactory =
            DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ITunesUException(e);
        }

        try {
            return docBuilder.parse(new InputSource(new StringReader(result)));
        } catch (SAXException e) {
            throw new ITunesUException(e);
        } catch (IOException e) {
            throw new ITunesUException(e);
        }
    }

    private String execute(String handle, String xml)
        throws ITunesUException {

        ITunesUFilePOST iTunesUFilePOST = new ITunesUFilePOST();
        try {
            return iTunesUFilePOST.invokeAction(this.getUploadUrl(handle, true),
                                                "file",
                                                "file.xml",
                                                xml,
                                                "text/xml");
        } catch (MessagingException e) {
            throw new ITunesUException(e);
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }
    }

    private String generateToken() throws ITunesUException {
    	ITunesU iTunesU = new ITunesU();

        String credentials = iTunesU.getCredentialsString(this.credentials);
        String identity = this.identity == null ? "" : this.identity;
        Date now = new Date();
        byte[] key;
        
        try {
            key = sharedSecret.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new ITunesUException(e);
        }

        String token = iTunesU.getAuthorizationToken(credentials, identity, now, key);
        return token;
    }
    
    private String getDestination(String handle) {
        String destination = this.getSiteDomain();

        if (handle != null) {
            destination += "." + handle;
        }

        if (this.debug) {
            destination += this.debugSuffix;
        }

        return destination;
    }

    private String getPrefix() {
        return this.siteUrl.substring(0, this.siteUrl.indexOf(".woa/") + 4);
    }

    private String getSiteDomain() {
        return this.siteUrl.substring(this.siteUrl.lastIndexOf('/') + 1);
    }
}

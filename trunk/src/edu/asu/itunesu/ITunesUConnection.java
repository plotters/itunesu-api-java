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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The iTunesU Web Services API connection.
 *
 * @author <a href="mailto:ramen@asu.edu">Dave Benjamin</a>
 */
public class ITunesUConnection {
    public static final String KEY_GROUP_MINIMAL = "minimal";
    public static final String KEY_GROUP_MOST    = "most";
    public static final String KEY_GROUP_MAXIMAL = "maximal";

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
     *
     * @param identity The identity as a string.
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
        return ITunesUResponse.fromXml(this.showTree(null)).getSite();
    }

    /**
     * Retrieves the entire site, loading a minimal amount of data.
     * Permissions and tracks are omitted. Only the name and handle
     * attributes are populated.
     *
     * @return A {@link Site} model object.
     */
    public Site getSiteMinimal() throws ITunesUException {
        return ITunesUResponse.fromXml(this.showTree(null, KEY_GROUP_MINIMAL)).getSite();
    }

    /**
     * Retrieves a section by its handle.
     *
     * @param handle The handle of the section.
     * @return A {@link Section} model object.
     */
    public Section getSection(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Section[Handle=" + handle + "]";
        Element element = getElementByXPath(xml, pattern);
        if (element == null) {
            throw new ITunesUException("not a Section handle");
        }
        return Section.fromXmlElement(element);
    }

    /**
     * Retrieves a division by its handle.
     *
     * @param handle The handle of the division.
     * @return A {@link Division} model object.
     */
    public Division getDivision(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Division[Handle=" + handle + "]";
        Element element = getElementByXPath(xml, pattern);
        if (element == null) {
            throw new ITunesUException("not a Division handle");
        }
        return Division.fromXmlElement(element);
    }

    /**
     * Retrieves a course by its handle.
     *
     * @param handle The handle of the course.
     * @return A {@link Course} model object.
     */
    public Course getCourse(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Course[Handle=" + handle + "]";
        Element element = getElementByXPath(xml, pattern);
        if (element == null) {
            throw new ITunesUException("not a Course handle");
        }
        return Course.fromXmlElement(element);
    }

    /**
     * Retrieves a group by its handle.
     *
     * @param handle The handle of the group.
     * @return A {@link Group} model object.
     */
    public Group getGroup(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Group[Handle=" + handle + "]";
        Element element = getElementByXPath(xml, pattern);
        if (element == null) {
            throw new ITunesUException("not a Group handle");
        }
        return Group.fromXmlElement(element);
    }

    /**
     * Retrieves a track by its handle.
     *
     * @param handle The handle of the track.
     * @return A {@link Track} model object.
     */
    public Track getTrack(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Track[Handle=" + handle + "]";
        Element element = getElementByXPath(xml, pattern);
        if (element == null) {
            throw new ITunesUException("not a Track handle");
        }
        return Track.fromXmlElement(element);
    }

    /**
     * Retrieves all sections beneath a node in the site tree.
     * 
     * @param handle The handle of a node in the tree, or null for the site.
     * @return A list of {@link Section} model objects.
     */
    public List<Section> getSections(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Section[Handle]";
        List<Element> elements = getElementsByXPath(xml, pattern);
        List<Section> result = new ArrayList<Section>();
        for (Element element : elements) {
            result.add(Section.fromXmlElement(element));
        }
        return result;
    }

    /**
     * Retrieves all divisions beneath a node in the site tree.
     * 
     * @param handle The handle of a node in the tree, or null for the site.
     * @return A list of {@link Division} model objects.
     */
    public List<Division> getDivisions(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Division[Handle]";
        List<Element> elements = getElementsByXPath(xml, pattern);
        List<Division> result = new ArrayList<Division>();
        for (Element element : elements) {
            result.add(Division.fromXmlElement(element));
        }
        return result;
    }

    /**
     * Retrieves all courses beneath a node in the site tree.
     * 
     * @param handle The handle of a node in the tree, or null for the site.
     * @return A list of {@link Course} model objects.
     */
    public List<Course> getCourses(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Course[Handle]";
        List<Element> elements = getElementsByXPath(xml, pattern);
        List<Course> result = new ArrayList<Course>();
        for (Element element : elements) {
            result.add(Course.fromXmlElement(element));
        }
        return result;
    }

    /**
     * Retrieves all groups beneath a node in the site tree.
     * 
     * @param handle The handle of a node in the tree, or null for the site.
     * @return A list of {@link Group} model objects.
     */
    public List<Group> getGroups(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Group[Handle]";
        List<Element> elements = getElementsByXPath(xml, pattern);
        List<Group> result = new ArrayList<Group>();
        for (Element element : elements) {
            result.add(Group.fromXmlElement(element));
        }
        return result;
    }

    /**
     * Retrieves all tracks beneath a node in the site tree.
     * 
     * @param handle The handle of a node in the tree, or null for the site.
     * @return A list of {@link Track} model objects.
     */
    public List<Track> getTracks(String handle) throws ITunesUException {
        String xml = this.showTree(handle);
        String pattern = "//Track[Handle]";
        List<Element> elements = getElementsByXPath(xml, pattern);
        List<Track> result = new ArrayList<Track>();
        for (Element element : elements) {
            result.add(Track.fromXmlElement(element));
        }
        return result;
    }

    /**
     * Updates site information. Same as calling mergeSite() with
     * mergeByHandle and destructive set to false.
     *
     * @param siteHandle Handle for the site to update.
     * @param site Object containing site information.
     */
    public ITunesUResponse mergeSite(String siteHandle, Site site)
        throws ITunesUException {

        return this.mergeSite(siteHandle, site, false, false);
    }

    /**
     * Updates site information. Same as calling mergeSite() with
     * destructive set to false.
     *
     * @param siteHandle Handle for the site to update.
     * @param site Object containing site information.
     * @param mergeByHandle If true, merge sections by handle.
     *                      Otherwise, merge by name.
     */
    public ITunesUResponse mergeSite(String siteHandle,
                                     Site site,
                                     boolean mergeByHandle)
        throws ITunesUException {

        return this.mergeSite(siteHandle, site, mergeByHandle, false);
    }

    /**
     * Updates site information.
     *
     * @param siteHandle Handle for the site to update.
     * @param site Object containing site information.
     * @param mergeByHandle If true, merge sections by handle.
     *                      Otherwise, merge by name.
     * @param destructive If true, delete any unspecified
     *                    sections or permissions.
     */
    public ITunesUResponse mergeSite(String siteHandle,
                                     Site site,
                                     boolean mergeByHandle,
                                     boolean destructive)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildMergeSite(siteHandle, site, mergeByHandle, destructive);
        return this.send(null, doc);
    }

    /**
     * Adds a division to a section.
     *
     * @param parentHandle Handle for the parent section.
     * @param templateHandle Handle for the division template, or null if none.
     * @param division Object containing division information.
     */
    public ITunesUResponse addDivision(String parentHandle,
                                       String templateHandle,            
                                       Division division)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildAddDivision(parentHandle, templateHandle, division);
        return this.send(null, doc);
    }

    /**
     * Deletes a division from a section.
     *
     * @param divisionHandle Handle for the division to delete.
     */
    public ITunesUResponse deleteDivision(String divisionHandle)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildDeleteDivision(divisionHandle);
        return this.send(null, doc);
    }

    /**
     * Updates division information. Same as calling mergeDivision() with
     * mergeByHandle and destructive set to false.
     *
     * @param divisionHandle Handle for the division to update.
     * @param division Object containing division information.
     */
    public ITunesUResponse mergeDivision(String divisionHandle, Division division)
        throws ITunesUException {

        return this.mergeDivision(divisionHandle, division, false, false);
    }

    /**
     * Updates division information. Same as calling mergeDivision() with
     * destructive set to false.
     *
     * @param divisionHandle Handle for the division to update.
     * @param division Object containing division information.
     * @param mergeByHandle If true, merge sections by handle.
     *                      Otherwise, merge by name.
     */
    public ITunesUResponse mergeDivision(String divisionHandle,
                                         Division division,
                                         boolean mergeByHandle)
        throws ITunesUException {

        return this.mergeDivision(divisionHandle, division, mergeByHandle, false);
    }

    /**
     * Updates division information.
     *
     * @param divisionHandle Handle for the division to update.
     * @param division Object containing division information.
     * @param mergeByHandle If true, merge sections by handle.
     *                      Otherwise, merge by name.
     * @param destructive If true, delete any unspecified
     *                    sections or permissions.
     */
    public ITunesUResponse mergeDivision(String divisionHandle,
                                         Division division,
                                         boolean mergeByHandle,
                                         boolean destructive)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildMergeDivision(divisionHandle, division, mergeByHandle, destructive);
        return this.send(null, doc);
    }

    /**
     * Adds a section to a site or division.
     *
     * @param parentHandle Handle for the parent site or division.
     * @param section Object containing section information.
     */
    public ITunesUResponse addSection(String parentHandle,
                                      Section section)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildAddSection(parentHandle, section);
        return this.send(null, doc);
    }

    /**
     * Deletes a section from a site or division.
     *
     * @param sectionHandle Handle for the section to delete.
     */
    public ITunesUResponse deleteSection(String sectionHandle)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildDeleteSection(sectionHandle);
        return this.send(null, doc);
    }

    /**
     * Updates section information. Same as calling mergeSection() with
     * mergeByHandle and destructive set to false.
     *
     * @param sectionHandle Handle for the section to update.
     * @param section Object containing section information.
     */
    public ITunesUResponse mergeSection(String sectionHandle, Section section)
        throws ITunesUException {

        return this.mergeSection(sectionHandle, section, false, false);
    }

    /**
     * Updates section information. Same as calling mergeSection() with
     * destructive set to false.
     *
     * @param sectionHandle Handle for the section to update.
     * @param section Object containing section information.
     * @param mergeByHandle If true, merge items by handle.
     *                      Otherwise, merge by name.
     */
    public ITunesUResponse mergeSection(String sectionHandle,
                                        Section section,
                                        boolean mergeByHandle)
        throws ITunesUException {

        return this.mergeSection(sectionHandle, section, mergeByHandle, false);
    }

    /**
     * Updates section information.
     *
     * @param sectionHandle Handle for the section to update.
     * @param section Object containing section information.
     * @param mergeByHandle If true, merge items by handle.
     *                      Otherwise, merge by name.
     * @param destructive If true, delete any unspecified
     *                    items or permissions.
     */
    public ITunesUResponse mergeSection(String sectionHandle,
                                        Section section,
                                        boolean mergeByHandle,
                                        boolean destructive)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildMergeSection(sectionHandle, section, mergeByHandle, destructive);
        return this.send(null, doc);
    }

    /**
     * Adds a course to a section.
     *
     * @param parentHandle Handle for the parent section.
     * @param templateHandle Handle for the course template, or null if none.
     * @param course Object containing course information.
     */
    public ITunesUResponse addCourse(String parentHandle,
                                     String templateHandle,
                                     Course course)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildAddCourse(parentHandle, templateHandle, course);
        return this.send(null, doc);
    }

    /**
     * Deletes a course from a section.
     *
     * @param courseHandle Handle for the course to delete.
     */
    public ITunesUResponse deleteCourse(String courseHandle)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildDeleteCourse(courseHandle);
        return this.send(null, doc);
    }

    /**
     * Updates course information. Same as calling mergeCourse() with
     * mergeByHandle and destructive set to false.
     *
     * @param courseHandle Handle for the course to update.
     * @param course Object containing course information.
     */
    public ITunesUResponse mergeCourse(String courseHandle, Course course)
        throws ITunesUException {

        return this.mergeCourse(courseHandle, course, false, false);
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
    public ITunesUResponse mergeCourse(String courseHandle,
                                       Course course,
                                       boolean mergeByHandle)
        throws ITunesUException {

        return this.mergeCourse(courseHandle, course, mergeByHandle, false);
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
    public ITunesUResponse mergeCourse(String courseHandle,
                                       Course course,
                                       boolean mergeByHandle,
                                       boolean destructive)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildMergeCourse(courseHandle, course, mergeByHandle, destructive);
        return this.send(null, doc);
    }

    /**
     * Adds a group (also called a tab) to a course.
     *
     * @param parentHandle Handle for the parent course.
     * @param group Object containing group information.
     */
    public ITunesUResponse addGroup(String parentHandle, Group group)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildAddGroup(parentHandle, group);
        return this.send(null, doc);
    }

    /**
     * Deletes a group from a course.
     *
     * @param groupHandle Handle for the group to delete.
     */
    public ITunesUResponse deleteGroup(String groupHandle)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildDeleteGroup(groupHandle);
        return this.send(null, doc);
    }

    /**
     * Updates group information. Same as calling mergeGroup() with
     * destructive set to false.
     *
     * @param groupHandle Handle for the group to update.
     * @param group Object containing group information.
     */
    public ITunesUResponse mergeGroup(String groupHandle, Group group)
        throws ITunesUException {

        return this.mergeGroup(groupHandle, group, false, false);
    }

    /**
     * Updates group information. Same as calling mergeGroup() with
     * destructive set to false.
     *
     * @param groupHandle Handle for the group to update.
     * @param group Object containing group information.
     * @param mergeByHandle If true, merge tracks by handle.
     *                      Otherwise, merge by name.
     */
    public ITunesUResponse mergeGroup(String groupHandle,
                                      Group group,
                                      boolean mergeByHandle)
        throws ITunesUException {

        return this.mergeGroup(groupHandle, group, mergeByHandle, false);
    }

    /**
     * Updates group information.
     *
     * @param groupHandle Handle for the group to update.
     * @param group Object containing group information
     * @param mergeByHandle If true, merge tracks by handle.
     *                      Otherwise, merge by name.
     * @param destructive If true, delete any unspecified
     *                    tracks or permissions.
     */
    public ITunesUResponse mergeGroup(String groupHandle,
                                      Group group,
                                      boolean mergeByHandle,
                                      boolean destructive)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildMergeGroup(groupHandle, group, mergeByHandle, destructive);
        return this.send(null, doc);
    }

    /**
     * Requests for a group to update its feed content
     *
     * @param groupHandle Handle for the group to update.
     */
    public ITunesUResponse updateGroup(String groupHandle)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildUpdateGroup(groupHandle);
        return this.send(null, doc);
    }

    /**
     * Adds a track to a group.
     *
     * @param parentHandle Handle for the parent group.
     * @param track Object containing track information.
     */
    public ITunesUResponse addTrack(String parentHandle,
                                    Track track)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildAddTrack(parentHandle, track);
        return this.send(null, doc);
    }

    /**
     * Deletes a track from a group.
     *
     * @param trackHandle Handle for the track to delete.
     */
    public ITunesUResponse deleteTrack(String trackHandle)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildDeleteTrack(trackHandle);
        return this.send(null, doc);
    }

    /**
     * Updates track information.
     *
     * @param trackHandle Handle for the track to update.
     * @param track Object containing track information.
     */
    public ITunesUResponse mergeTrack(String trackHandle,
                                      Track track)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildMergeTrack(trackHandle, track);
        return this.send(null, doc);
    }

    /**
     * Adds a permission to a section, course, or group.
     *
     * @param parentHandle Handle for the parent section, course, or group.
     * @param permission Object containing permission information.
     */
    public ITunesUResponse addPermission(String parentHandle,
                                         Permission permission)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildAddPermission(parentHandle, permission);
        return this.send(null, doc);
    }

    /**
     * Deletes a permission from a section, course, or group.
     *
     * @param parentHandle Handle for the parent section, course, or group.
     * @param credential Credential of permission to delete.
     */
    public ITunesUResponse deletePermission(String parentHandle,
                                            String credential)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildDeletePermission(parentHandle, credential);
        return this.send(null, doc);
    }

    /**
     * Updates a permission.
     *
     * @param parentHandle Handle for the parent section, course, or group.
     * @param permission Object containing permission information.
     */
    public ITunesUResponse mergePermission(String parentHandle,
                                           Permission permission)
        throws ITunesUException {

        ITunesUDocument doc = ITunesUDocument.buildMergePermission(parentHandle, permission);
        return this.send(null, doc);
    }

    /**
     * Reads XML for an element by its handle. This version of ShowTree uses
     * the simple URL-based (/API/ShowTree/) retrieval method, which produces
     * the same results as the "most" key group.
     *
     * @param handle Handle of the parent element, or null for the whole site.
     * @return An XML string.
     */
    public String showTree(String handle) throws ITunesUException {
        String prefix = this.getPrefix();
        String url = prefix + "/API/ShowTree/" + this.getDestination(handle);

        ITunesU iTunesU = new ITunesU();

        try {
            if (this.debug) System.err.println("Request URL:\n" + url);
            String response = iTunesU.invokeAction(url, this.generateToken());
            if (this.debug) System.err.println("Response Body:\n" + response);
            return response;
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }
    }

    /**
     * Reads XML for an element by its handle, specifying a key group. This
     * version of ShowTree uses the XML-based ITunesUDocument retrieval method.
     *
     * @param handle Handle of the parent element, or null for the whole site.
     * @param keyGroup Must be one of: minimal, most, maximal
     * @return An XML string.
     */
    public String showTree(String handle, String keyGroup)
        throws ITunesUException {

        if (handle == null) {
            handle = "";
        }
        ITunesUDocument doc = ITunesUDocument.buildShowTree(handle, keyGroup);

        try {
            return this.execute(null, doc.toXml());
        } catch (ParserConfigurationException e) {
            throw new ITunesUException(e);
        } catch (TransformerException e) {
            throw new ITunesUException(e);
        }
    }

    /**
     * Reads RSS feed XML for an element by its handle.
     *
     * @param handle Handle of the element.
     * @return An XML string.
     */
    public String showFeed(String handle) throws ITunesUException {
        String prefix = this.getPrefix();
        String url = prefix + "/Feed/" + this.getDestination(handle);

        ITunesU iTunesU = new ITunesU();

        try {
            if (this.debug) System.err.println("Request URL:\n" + url);
            String response = iTunesU.invokeAction(url, this.generateToken());
            if (this.debug) System.err.println("Response Body:\n" + response);
            return response;
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }
    }

    /**
     * Returns a CSV report of daily activity.
     *
     * @param startDate Start date in YYYY-MM-DD format.
     * @param endDate End date in YYYY-MM-DD format, or null.
     * @return A string containing CSV data.
     */
    public String getDailyReportLogs(String startDate, String endDate)
        throws ITunesUException {

        ITunesU iTunesU = new ITunesU();

        String url = (this.getPrefix()
                      + "/API/GetDailyReportLogs/"
                      + this.getDestination(null)
                      + "?StartDate=" + startDate);

        if (endDate != null) {
            url += "&EndDate=" + endDate;
        }

        try {
            if (this.debug) System.err.println("Request URL:\n" + url);
            String response = iTunesU.invokeAction(url, this.generateToken());
            if (this.debug) System.err.println("Response Body:\n" + response);
            return response;
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }
    }

    /**
     * Generates and returns a new iTunesU upload URL.
     *
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
            if (this.debug) System.err.println("Request URL:\n" + url);
            String response = iTunesU.invokeAction(url, this.generateToken());
            if (this.debug) System.err.println("Response Body:\n" + response);
            return response;
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }
    }

    /**
     * Uploads file content to iTunesU.
     *
     * @param handle Handle for the destination.
     * @param content A File object containing the content to upload.
     */
    public String uploadContent(String handle,
                                File content) throws ITunesUException {
        ITunesUFilePOST iTunesUFilePOST = new ITunesUFilePOST();
        String uploadUrl = this.getUploadUrl(handle, false);

        String response;

        try {
            response = iTunesUFilePOST.invokeAction(uploadUrl,
                                                    "file",
                                                    content,
                                                    "application/octet-stream");
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        } catch (FileNotFoundException e) {
            throw new ITunesUException(e);
        }

        if ("!".equals(response)) {
            throw new ITunesUException("Error uploading content");
        } else {
            return response;
        }
    }

    /**
     * Uploads file content to iTunesU.
     *
     * @param handle Handle for the destination.
     * @param fileName Name of the file to upload.
     * @param content Stream of the file content.
     * @param contentLength Length of the file, in bytes.
     */
    public String uploadContent(String handle,
                                String fileName,
                                InputStream content,
                                int contentLength) throws ITunesUException {
        ITunesUFilePOST iTunesUFilePOST = new ITunesUFilePOST();
        String uploadUrl = this.getUploadUrl(handle, false);

        String response;

        try {
            response = iTunesUFilePOST.invokeAction(uploadUrl,
                                                    "file",
                                                    fileName,
                                                    content,
                                                    contentLength,
                                                    "application/octet-stream");
        } catch (AssertionError e) {
            throw new ITunesUException(e);
        }

        if ("!".equals(response)) {
            throw new ITunesUException("Error uploading content");
        } else {
            return response;
        }
    }

    private ITunesUResponse send(String handle, ITunesUDocument doc)
        throws ITunesUException {

        String result;

        try {
            result = this.execute(handle, doc.toXml());
        } catch (ParserConfigurationException e) {
            throw new ITunesUException(e);
        } catch (TransformerException e) {
            throw new ITunesUException(e);
        }

        ITunesUResponse response = ITunesUResponse.fromXml(result);
        if (response.getError() != null && !"".equals(response.getError())) {
            throw new ITunesUException(response.getError());
        } else {
            return response;
        }
    }

    private String execute(String handle, String xml)
        throws ITunesUException {

        String url = this.getUploadUrl(handle, true);

        ITunesUFilePOST iTunesUFilePOST = new ITunesUFilePOST();
        try {
            if (this.debug) System.err.println("Request URL:\n" + url);
            if (this.debug) System.err.println("Request Body:\n" + xml);
            String response = iTunesUFilePOST.invokeAction(url,
                                                           "file",
                                                           "file.xml",
                                                           xml,
                                                           "text/xml");
            if (this.debug) System.err.println("Response Body:\n" + response);
            return response;
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

    private static List<Element> getElementsByXPath(String xml, String pattern)
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
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr;
        try {
            expr = xpath.compile(pattern);
        } catch (XPathExpressionException e) {
            throw new ITunesUException(e);
        }
        NodeList nodeList;
        try {
            nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new ITunesUException(e);
        }
        List<Element> result = new ArrayList<Element>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            result.add((Element) nodeList.item(i));
        }
        return result;
    }
    
    private static Element getElementByXPath(String xml, String pattern)
        throws ITunesUException {
        List<Element> elements = getElementsByXPath(xml, pattern);
        if (elements.size() > 0) {
            return elements.get(0);
        } else {
            return null;
        }
    }
}

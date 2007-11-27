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
 * A multimedia document.
 */
public class Track implements ITunesUElement {
    private String name;
    private String handle;
    private String kind;
    private Integer trackNumber;
    private Integer discNumber;
    private Long durationMilliseconds;
    private String albumName;
    private String artistName;
    private String genreName;
    private String comment;
    private String downloadUrl;

    public Track() {}

    public Track(String name,
                 String handle,
                 String kind,
                 Integer trackNumber,
                 Integer discNumber,
                 Long durationMilliseconds,
                 String albumName,
                 String artistName,
                 String genreName,
                 String comment,
                 String downloadUrl) {
        this.name = name;
        this.handle = handle;
        this.kind = kind;
        this.trackNumber = trackNumber;
        this.discNumber = discNumber;
        this.durationMilliseconds = durationMilliseconds;
        this.albumName = albumName;
        this.artistName = artistName;
        this.genreName = genreName;
        this.comment = comment;
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return this.name;
    }

    public String getHandle() {
        return this.handle;
    }

    public String getKind() {
        return this.kind;
    }

    public Integer getTrackNumber() {
        return this.trackNumber;
    }

    public Integer getDiscNumber() {
        return this.discNumber;
    }

    public Long getDurationMilliseconds() {
        return this.durationMilliseconds;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public String getGenreName() {
        return this.genreName;
    }

    public String getComment() {
        return this.comment;
    }

    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public void setDiscNumber(Integer discNumber) {
        this.discNumber = discNumber;
    }

    public void setDurationMilliseconds(Long durationMilliseconds) {
        this.durationMilliseconds = durationMilliseconds;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Element toXmlElement(Document doc) {
        Element element = doc.createElement("Track");
        if (this.name != null) {
            Element nameElement = doc.createElement("Name");
            nameElement.setTextContent(this.name);
            element.appendChild(nameElement);
            // Workaround for Apple bug - see:
            // http://discussions.apple.com/thread.jspa?threadID=1228047&tstart=0
            // Element titleElement = doc.createElement("Title");
            // titleElement.setTextContent(this.name);
            // element.appendChild(titleElement);
        }
        if (this.handle != null) {
            Element handleElement = doc.createElement("Handle");
            handleElement.setTextContent(this.handle);
            element.appendChild(handleElement);
        }
        if (this.kind != null) {
            Element kindElement = doc.createElement("Kind");
            kindElement.setTextContent(this.kind);
            element.appendChild(kindElement);
        }
        if (this.trackNumber != null) {
            Element trackNumberElement = doc.createElement("TrackNumber");
            trackNumberElement.setTextContent(Integer.toString(this.trackNumber));
            element.appendChild(trackNumberElement);
        }
        if (this.discNumber != null) {
            Element discNumberElement = doc.createElement("DiscNumber");
            discNumberElement.setTextContent(Integer.toString(this.discNumber));
            element.appendChild(discNumberElement);
        }
        if (this.durationMilliseconds != null) {
            Element durationMillisecondsElement = doc.createElement("DurationMilliseconds");
            durationMillisecondsElement.setTextContent(Long.toString(this.durationMilliseconds));
            element.appendChild(durationMillisecondsElement);
        }
        if (this.albumName != null) {
            Element albumNameElement = doc.createElement("AlbumName");
            albumNameElement.setTextContent(this.albumName);
            element.appendChild(albumNameElement);
        }
        if (this.artistName != null) {
            Element artistNameElement = doc.createElement("ArtistName");
            artistNameElement.setTextContent(this.artistName);
            element.appendChild(artistNameElement);
        }
        if (this.genreName != null) {
            Element genreNameElement = doc.createElement("GenreName");
            genreNameElement.setTextContent(this.genreName);
            element.appendChild(genreNameElement);
        }
        if (this.comment != null) {
            Element commentElement = doc.createElement("Comment");
            commentElement.setTextContent(this.comment);
            element.appendChild(commentElement);
        }
        if (this.downloadUrl != null) {
            Element downloadUrlElement = doc.createElement("DownloadURL");
            downloadUrlElement.setTextContent(this.downloadUrl);
            element.appendChild(downloadUrlElement);
        }
        return element;
    }

    public static Track fromXmlElement(Element element) throws ITunesUException {
        if (!"Track".equals(element.getNodeName())) {
            throw new ITunesUException("Expected Track, got "
                                       + element.getNodeName());
        }
        String name = null;
        String handle = null;
        String kind = null;
        Integer trackNumber = 0;
        Integer discNumber = 1;
        Long durationMilliseconds = 0L;
        String albumName = null;
        String artistName = null;
        String genreName = null;
        String comment = null;
        String downloadUrl = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                if ("Name".equals(childNode.getNodeName())) {
                    name = childNode.getTextContent();
                } else if ("Handle".equals(childNode.getNodeName())) {
                    handle = childNode.getTextContent();
                } else if ("Kind".equals(childNode.getNodeName())) {
                    kind = childNode.getTextContent();
                } else if ("TrackNumber".equals(childNode.getNodeName())) {
                    trackNumber = Integer.parseInt(childNode.getTextContent());
                } else if ("DiscNumber".equals(childNode.getNodeName())) {
                    discNumber = Integer.parseInt(childNode.getTextContent());
                } else if ("DurationMilliseconds".equals(childNode.getNodeName())) {
                    durationMilliseconds = Long.parseLong(childNode.getTextContent());
                } else if ("AlbumName".equals(childNode.getNodeName())) {
                    albumName = childNode.getTextContent();
                } else if ("ArtistName".equals(childNode.getNodeName())) {
                    artistName = childNode.getTextContent();
                } else if ("GenreName".equals(childNode.getNodeName())) {
                    genreName = childNode.getTextContent();
                } else if ("Comment".equals(childNode.getNodeName())) {
                    comment = childNode.getTextContent();
                } else if ("DownloadURL".equals(childNode.getNodeName())) {
                    downloadUrl = childNode.getTextContent();
                }
            }
        }
        return new Track(name,
                         handle,
                         kind,
                         trackNumber,
                         discNumber,
                         durationMilliseconds,
                         albumName,
                         artistName,
                         genreName,
                         comment,
                         downloadUrl);
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

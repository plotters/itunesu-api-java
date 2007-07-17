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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

class MultipartFormBuilder {
    public static void addMultipartFormBody(MimeMultipart multipart,
                                            String name,
                                            String fileName,
                                            String data,
                                            String contentType)
        throws MessagingException {

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setDisposition("form-data; name=" + name);
        bodyPart.setFileName(fileName);
        bodyPart.setText(data);
        bodyPart.setHeader("Content-Type", contentType);
        multipart.addBodyPart(bodyPart);
    }

    public static String getMultipartBoundary(MimeMultipart multipart)
        throws MessagingException {

        Pattern pattern = Pattern.compile(".*boundary=\"([^\"]+)\"",
                                          Pattern.DOTALL);
        Matcher matcher = pattern.matcher(multipart.getContentType());

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new MessagingException("unable to find content boundary in "
                                         + multipart.getContentType());
        }
    }

    public static String getMultipartData(MimeMultipart multipart)
        throws MessagingException, IOException {

        MimeBodyPart root = new MimeBodyPart();
        root.setContent(multipart);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        root.writeTo(output);
        return output.toString();
    }

    public static void main(String[] args) throws Exception {
        MimeMultipart multipart = new MimeMultipart();

        addMultipartFormBody(multipart,
                             "file",
                             "data.xml",
                             "<hey></hey>",
                             "text/xml");

        System.out.println("Output:\n------\n");
        System.out.print("Content-Type: multipart/form-data; boundary="
                         + getMultipartBoundary(multipart) + "\r\n");
        System.out.print(getMultipartData(multipart) + "\r\n");
    }
}

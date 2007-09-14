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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

class ITunesUFilePOST {
    public String invokeAction(String url,
                               String name,
                               String fileName,
                               String data,
                               String contentType) throws MessagingException {

        // Send a request to iTunes U and record the response.
        StringBuffer response = null;

        try {
            // Verify that the communication will be over SSL.
            if (!url.startsWith("https")) {
                throw new MalformedURLException("ITunesUFilePOST.invokeAction(): URL \""
                                                + url + "\" does not use HTTPS.");
            }

            // Build the multipart data.
            MimeMultipart multipart = new MimeMultipart();
            MultipartFormBuilder.addMultipartFormBody(multipart,
                                                      name,
                                                      fileName,
                                                      data,
                                                      contentType);

            // Create a connection to the requested iTunes U URL.
            HttpURLConnection connection =
                (HttpURLConnection) new URL(url).openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                                          "multipart/form-data; boundary="
                                          + MultipartFormBuilder.getMultipartBoundary(multipart));

            String multipartData = MultipartFormBuilder.getMultipartData(multipart);
            
            // Send the multipart data to iTunes U.
            connection.connect();
            OutputStream output = connection.getOutputStream();
            output.write(multipartData.getBytes("UTF-8"));
            output.flush();
            output.close();

            // Read iTunes U's response.
            response = new StringBuffer();
            InputStream input = connection.getInputStream();
            Reader reader = new InputStreamReader(input, "UTF-8");
            reader = new BufferedReader(reader);
            char[] buffer = new char[16 * 1024];
            for (int n = 0; n >= 0;) {
                n = reader.read(buffer, 0, buffer.length);
                if (n > 0) response.append(buffer, 0, n);
            }

            // Clean up.
            input.close();
            connection.disconnect();

        } catch (UnsupportedEncodingException e) {
            // ITunes U requires UTF-8 and ASCII encoding support.
            throw new java.lang.AssertionError("ITunesUFilePOST.invokeAction(): UTF-8 encoding not supported!");

        } catch (IOException e) {
            // Report communication problems.
            throw new java.lang.AssertionError("ITunesUFilePOST.invokeAction(): I/O Exception " + e);
        }

        // Return the response received from iTunes U.
        return response.toString();
    }
}

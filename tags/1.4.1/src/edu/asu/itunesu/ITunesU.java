/*
 * This file was modified to contain a "package" directive and use package
 * scope for the ITunesU class. No other changes were made.
 *
 * Dave Benjamin, Arizona State University
 */

/*
 * Copyright 2006 Apple Computer, Inc.  All rights reserved.
 * 
 * iTunes U Sample Code License
 * IMPORTANT:  This Apple software is supplied to you by Apple Computer, Inc. ("Apple") 
 * in consideration of your agreement to the following terms, and your use, 
 * installation, modification or distribution of this Apple software constitutes 
 * acceptance of these terms.  If you do not agree with these terms, please do not use, 
 * install, modify or distribute this Apple software.
 * 
 * In consideration of your agreement to abide by the following terms and subject to
 * these terms, Apple grants you a personal, non-exclusive, non-transferable license, 
 * under Apple's copyrights in this original Apple software (the "Apple Software"): 
 * 
 * (a) to internally use, reproduce, modify and internally distribute the Apple 
 * Software, with or without modifications, in source and binary forms, within your 
 * educational organization or internal campus network for the sole purpose of 
 * integrating Apple's iTunes U software with your internal campus network systems; and 
 * 
 * (b) to redistribute the Apple Software to other universities or educational 
 * organizations, with or without modifications, in source and binary forms, for the 
 * sole purpose of integrating Apple's iTunes U software with their internal campus 
 * network systems; provided that the following conditions are met:
 * 
 *  -  If you redistribute the Apple Software in its entirety and without 
 *     modifications, you must retain the above copyright notice, this entire license 
 *     and the disclaimer provisions in all such redistributions of the Apple Software.
 *  -  If you modify and redistribute the Apple Software, you must indicate that you
 *     have made changes to the Apple Software, and you must retain the above
 *     copyright notice, this entire license and the disclaimer provisions in all
 *     such redistributions of the Apple Software and/or derivatives thereof created
 *     by you.
 *     -  Neither the name, trademarks, service marks or logos of Apple may be used to 
 *     endorse or promote products derived from the Apple Software without specific 
 *     prior written permission from Apple.  
 * 
 * Except as expressly stated above, no other rights or licenses, express or implied, 
 * are granted by Apple herein, including but not limited to any patent rights that may
 * be infringed by your derivative works or by other works in which the Apple Software 
 * may be incorporated.  THE APPLE SOFTWARE IS PROVIDED BY APPLE ON AN "AS IS" BASIS.  
 * APPLE MAKES NO WARRANTIES, EXPRESS OR IMPLIED, AND HEREBY DISCLAIMS ALL WARRANTIES, 
 * INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE 
 * OR ITS USE AND OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS OR SYSTEMS.  
 * APPLE IS NOT OBLIGATED TO PROVIDE ANY MAINTENANCE, TECHNICAL OR OTHER SUPPORT FOR 
 * THE APPLE SOFTWARE, OR TO PROVIDE ANY UPDATES TO THE APPLE SOFTWARE.  IN NO EVENT 
 * SHALL APPLE BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR DISTRIBUTION 
 * OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT 
 * (INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.         
 * 
 * Rev.  120806                                             
 * 
 */

package edu.asu.itunesu;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

/**
 * The <CODE>ITunesU</CODE> class permits the secure transmission
 * of user credentials and identity between an institution's
 * authentication and authorization system and iTunes U.<P>
 * 
 * The code in this class can be tested by
 * running it with the following commands:
 * <PRE>
 *     javac ITunesU.java
 *     java ITunesU</PRE>
 *
 * Changes to values defined in this class' main() method must
 * be made before it will succesfully communicate with iTunes U.
 *
 */
class ITunesU extends Object {

    /**
     * Generate the HMAC-SHA256 signature of a message string, as defined in
     * <A HREF="http://www.ietf.org/rfc/rfc2104.txt">RFC 2104</A>.
     *
     * @param message The string to sign.
     * @param key The bytes of the key to sign it with.
     *
     * @return A hexadecimal representation of the signature.
     */
    public String hmacSHA256(String message, byte[] key) {

        // Start by getting an object to generate SHA-256 hashes with.
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new java.lang.AssertionError(
                    this.getClass().getName()
                    + ".hmacSHA256(): SHA-256 algorithm not found!");
        }

        // Hash the key if necessary to make it fit in a block (see RFC 2104).
        if (key.length > 64) {
            sha256.update(key);
            key = sha256.digest();
            sha256.reset();
        }

        // Pad the key bytes to a block (see RFC 2104).
        byte block[] = new byte[64];
        for (int i = 0; i < key.length; ++i) block[i] = key[i];
        for (int i = key.length; i < block.length; ++i) block[i] = 0;

        // Calculate the inner hash, defined in RFC 2104 as
        // SHA-256(KEY ^ IPAD + MESSAGE)), where IPAD is 64 bytes of 0x36.
        for (int i = 0; i < 64; ++i) block[i] ^= 0x36;
        sha256.update(block);
        try {
            sha256.update(message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new java.lang.AssertionError(
                    "ITunesU.hmacSH256(): UTF-8 encoding not supported!");
        }
        byte[] hash = sha256.digest();
        sha256.reset();

        // Calculate the outer hash, defined in RFC 2104 as
        // SHA-256(KEY ^ OPAD + INNER_HASH), where OPAD is 64 bytes of 0x5c.
        for (int i = 0; i < 64; ++i) block[i] ^= (0x36 ^ 0x5c);
        sha256.update(block);
        sha256.update(hash);
        hash = sha256.digest();

        // The outer hash is the message signature...
        // convert its bytes to hexadecimals.
        char[] hexadecimals = new char[hash.length * 2];
        for (int i = 0; i < hash.length; ++i) {
            for (int j = 0; j < 2; ++j) {
                int value = (hash[i] >> (4 - 4 * j)) & 0xf;
                char base = (value < 10) ? ('0') : ('a' - 10);
                hexadecimals[i * 2 + j] = (char)(base + value);
            }
        }

        // Return a hexadecimal string representation of the message signature.
        return new String(hexadecimals);

    }

    /**
     * Combine user credentials into an appropriately formatted string.
     *
     * @param credentials An array of credential strings. Credential
     *                    strings may contain any character but ';'
     *                    (semicolon), '\\' (backslash), and control
     *                    characters (with ASCII codes 0-31 and 127).
     *
     * @return <CODE>null</CODE> if and only if any of the credential strings 
     *         are invalid.
     */
    public String getCredentialsString(String[] credentials) {

        // Create a buffer with which to generate the credentials string.
        StringBuffer buffer = new StringBuffer();

        // Verify and add each credential to the buffer.
        if (credentials != null) {
            for (int i = 0; i < credentials.length; ++i) {
                if (i > 0) buffer.append(';');
                for (int j = 0, n = credentials[i].length(); j < n; ++j) {
                    char c = credentials[i].charAt(j);
                    if (c != ';' && c != '\\' && c >= ' ' && c != 127) {
                        buffer.append(c);
                    } else {
                        return null;
                    }
                }
            }
        }

        // Return the credentials string.
        return buffer.toString();

    }

    /**
     * Combine user identity information into an appropriately formatted string.
     *
     * @param displayName The user's name (optional).
     * @param emailAddress The user's email address (optional).
     * @param username The user's username (optional).
     * @param userIdentifier A unique identifier for the user (optional).
     *
     * @return A non-<CODE>null</CODE> user identity string.
     */
    public String getIdentityString(String displayName, String emailAddress,
                                    String username, String userIdentifier) {

        // Create a buffer with which to generate the identity string.
        StringBuffer buffer = new StringBuffer();

        // Define the values and delimiters of each of the string's elements.
        String[] values = { displayName, emailAddress,
                            username, userIdentifier };
        char[][] delimiters = { { '"', '"' }, { '<', '>' },
                                { '(', ')' }, { '[', ']' } };

        // Add each element to the buffer, escaping
        // and delimiting them appropriately.
        for (int i = 0; i < values.length; ++i) {
            if (values[i] != null) {
                if (buffer.length() > 0) buffer.append(' ');
                buffer.append(delimiters[i][0]);
                for (int j = 0, n = values[i].length(); j < n; ++j) {
                    char c = values[i].charAt(j);
                    if (c == delimiters[i][1] || c == '\\') buffer.append('\\');
                    buffer.append(c);
                }
                buffer.append(delimiters[i][1]);
            }
        }

        // Return the generated string.
        return buffer.toString();

    }

    /**
     * Generate an iTunes U digital signature for a user's credentials
     * and identity. Signatures are usually sent to iTunes U along
     * with the credentials, identity, and a time stamp to warrant
     * to iTunes U that the credential and identity values are
     * officially sanctioned. For such uses, it will usually makes
     * more sense to use an authorization token obtained from the
     * {@link #getAuthorizationToken(java.lang.String, java.lang.String, java.util.Date, byte[])}
     * method than to use a signature directly: Authorization
     * tokens include the signature but also the credentials, identity,
     * and time stamp, and have those conveniently packaged in
     * a format that is easy to send to iTunes U over HTTPS.
     *
     * @param credentials The user's credentials string, as
     *                    obtained from getCredentialsString().
     * @param identity The user's identity string, as
     *                 obtained from getIdentityString().
     * @param time Signature time stamp.
     * @param key The bytes of your institution's iTunes U shared secret key.
     *
     * @return A hexadecimal representation of the signature.
     */
    public String getSignature(String credentials, String identity,
                               Date time, byte[] key) {

        // Create a buffer in which to format the data to sign.
        StringBuffer buffer = new StringBuffer();

        // Generate the data to sign.
        try {

            // Start with the appropriately encoded credentials.
            buffer.append("credentials=");
            buffer.append(URLEncoder.encode(credentials, "UTF-8"));

            // Add the appropriately encoded identity information.
            buffer.append("&identity=");
            buffer.append(URLEncoder.encode(identity, "UTF-8"));

            // Add the appropriately formatted time stamp. Note that
            // the time stamp is expressed in seconds, not milliseconds.
            buffer.append("&time=");
            buffer.append(time.getTime() / 1000);

        } catch (UnsupportedEncodingException e) {

            // UTF-8 encoding support is required.
            throw new java.lang.AssertionError(
                "ITunesU.getSignature():  UTF-8 encoding not supported!");

        }

        // Generate and return the signature.
        String signature = this.hmacSHA256(buffer.toString(), key);
        return signature;

    }

    /**
     * Generate and sign an authorization token that you can use to securely
     * communicate to iTunes U a user's credentials and identity. The token
     * includes all the data you need to communicate to iTunes U as well as
     * a creation time stamp and a digital signature for the data and time.
     *
     * @param credentials The user's credentials string, as
     *                    obtained from getCredentialsString().
     * @param identity The user's identity string, as
     *                 obtained from getIdentityString().
     * @param time Token time stamp. The token will only be valid from
     *             its time stamp time and for a short time thereafter
     *             (usually 90 seconds).
     * @param key The bytes of your institution's iTunes U shared secret key.
     *
     * @return The authorization token. The returned token will
     *         be URL-encoded and can be sent to iTunes U with
     *         a <A HREF="http://www.ietf.org/rfc/rfc1866.txt">form
     *         submission</A>. iTunes U will typically respond with
     *         HTML that should be sent to the user's browser.
     */
    public String getAuthorizationToken(String credentials, String identity,
                                        Date time, byte[] key) {

        // Create a buffer with which to generate the authorization token.
        StringBuffer buffer = new StringBuffer();

        // Generate the authorization token.
        try {

            // Start with the appropriately encoded credentials.
            buffer.append("credentials=");
            buffer.append(URLEncoder.encode(credentials, "UTF-8"));

            // Add the appropriately encoded identity information.
            buffer.append("&identity=");
            buffer.append(URLEncoder.encode(identity, "UTF-8"));

            // Add the appropriately formatted time stamp. Note that
            // the time stamp is expressed in seconds, not milliseconds.
            buffer.append("&time=");
            buffer.append(time.getTime() / 1000);

            // Generate and add the token signature.
            String data = buffer.toString();
            buffer.append("&signature=");
            buffer.append(this.hmacSHA256(data, key));

        } catch (UnsupportedEncodingException e) {

            // UTF-8 encoding support is required.
            throw new java.lang.AssertionError(
                    "ITunesU.getAuthorizationToken(): "
                    + "UTF-8 encoding not supported!");

        }

        // Return the signed authorization token.
        return buffer.toString();

    }

    /**
     * Send a request for an action to iTunes U with an authorization token. 
     *
     * @param url URL defining how to communicate with iTunes U and
     *            identifying which iTunes U action to invoke and which iTunes
     *            U page or item to apply the action to. Such URLs have a
     *            format like <CODE>[PREFIX]/[ACTION]/[DESTINATION]</CODE>,
     *            where <CODE>[PREFIX]</CODE> is a value like
     *            "https://deimos.apple.com/WebObjects/Core.woa" which defines
     *            how to communicate with iTunes U, <CODE>[ACTION]</CODE>
     *            is a value like "Browse" which identifies which iTunes U
     *            action to invoke, and <CODE>[DESTINATION]</CODE> is a value
     *            like "example.edu" which identifies which iTunes U page
     *            or item to apply the action to. The destination string
     *            "example.edu" refers to the root page of the iTunes U site
     *            identified by the domain "example.edu". Destination strings
     *            for other items within that site contain the site domain
     *            followed by numbers separated by periods. For example:
     *            "example.edu.123.456.0789". You can find these
     *            strings in the items' URLs, which you can obtain from
     *            iTunes. See the iTunes U documentation for details.
     * @param token Authorization token generated by getAuthorizationToken().
     *
     * @return The iTunes U response, which may be HTML or
     *         text depending on the type of action invoked.
     */
    public String invokeAction(String url, String token) {

        // Send a request to iTunes U and record the response.
        StringBuffer response = null;
        try {

            // Verify that the communication will be over SSL.
            if (!url.startsWith("https")) {
                throw new MalformedURLException(
                        "ITunesU.invokeAction(): URL \""
                        + url + "\" does not use HTTPS.");
            }

            // Create a connection to the requested iTunes U URL.
            HttpURLConnection connection =
                    (HttpURLConnection)new URL(url).openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8");

            // Send the authorization token to iTunes U.
            connection.connect();
            OutputStream output = connection.getOutputStream();
            output.write(token.getBytes("UTF-8"));
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
            throw new java.lang.AssertionError(
                    "ITunesU.invokeAction(): UTF-8 encoding not supported!");

        } catch (IOException e) {

            // Report communication problems.
            throw new java.lang.AssertionError(
                    "ITunesU.invokeAction(): I/O Exception " + e);

        }

        // Return the response received from iTunes U.
        return response.toString();

    }

    /**
     * iTunes U credential and identity transmission sample. When your
     * itunes U site is initially created, Apple will send your institution's
     * technical contact a welcome email with a link to an iTunes U page
     * containing the following information, which you will need to customize
     * this method's code for your site: <P><DD><DL><DT><B>
     *
     * Information:</B><DD><CODE>
     *   Site URL</CODE> - The URL to your site in iTunes U. The last
     *                     component of that URL, after the last slash,
     *                     is a domain name that uniquely identifies your
     *                     site within iTunes U.<DD><CODE>
     *   shared secret</CODE> - A secret key known only to you and Apple that
     *                          allows you to control who has access to your
     *                          site and what access they have to it.<DD><CODE>
     *   debug suffix</CODE> - A suffix you can append to your site URL
     *                         to obtain debugging information about the
     *                         transmission of credentials and identity
     *                         information from your institution's
     *                         authentication and authorization services
     *                         to iTunes U.<DD><CODE>
     *   administrator credential</CODE> - The credential string to assign
     *                                     to users who should have the
     *                                     permission to administer your
     *                                     iTunes U site.</DL></DD><P><DD>
     *
     * Once you have substitute the information above in this method's code
     * as indicated in the code's comments, this method will connect
     * to iTunes U and obtain from it the HTML that needs to be returned to a
     * user's web browser to have a particular page or item in your iTunes U
     * site displayed to that user in iTunes. You can modify this method to
     * instead output the URL that would need to be opened to have that page
     * or item displayed in iTunes.</DD>
     */
    public static void main(String argv[]) {

        // Define your site's information. Replace these
        // values with ones appropriate for your site.
        String siteURL =
            "https://deimos.apple.com/WebObjects/Core.woa/Browse/example.edu";
        // String debugSuffix = "/abc123";
        String sharedSecret = "STRINGOFTHIRTYTWOLETTERSORDIGITS";
        String administratorCredential =
            "Administrator@urn:mace:itunesu.com:sites:example.edu";

        // Define the user information. Replace the credentials with the
        // credentials you want to grant to the current user, and the
        // optional identity information with the identity of that user.
        // For initial testing and site setup, use the singe administrator 
        // credential defined when your iTunes U site was created. Once
        // you have access to your iTunes U site, you will be able to define
        // additional credentials and the iTunes U access they provide.
        String[] credentialsArray = { administratorCredential };
        String displayName = "Jane Doe";
        String emailAddress = "janedoe@example.edu";
        String username = "jdoe";
        String userIdentifier = "42";

        // Define the iTunes U page to browse. Use the domain name that
        // uniquely identifies your site in iTunes U to browse to that site's
        // root page; use a destination string extracted from an iTunes U URL
        // to browse to another iTunes U page; or use a destination string
        // supplied as the "destination" parameter if this program is being
        // invoked as a part of the login web service for your iTunes U site.
        String siteDomain = siteURL.substring(siteURL.lastIndexOf('/') + 1);
        String destination = siteDomain;
        
        // Append your site's debug suffix to the destination if you want
        // to receive an HTML page providing information about the
        // transmission of credentials and identity between this program
        // and iTunes U. Uncomment the following line for testing only.
        //destination = destination + debugSuffix;

        // Use an ITunesU instance to format the credentials and identity
        // strings and to generate an authorization token for them.
        ITunesU iTunesU = new ITunesU();
        String identity = iTunesU.getIdentityString(displayName, emailAddress,
                                                    username, userIdentifier);
        String credentials = iTunesU.getCredentialsString(credentialsArray);
        Date now = new Date();
        byte[] key = null;
        try {
            key = sharedSecret.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new java.lang.AssertionError(
                    "ITunesU.hmacSH256(): US-ASCII encoding not supported!");
        }
        String token = iTunesU.getAuthorizationToken(credentials, identity,
                                                     now, key);

        // Use the authorization token to connect to iTunes U and obtain
        // from it the HTML that needs to be returned to a user's web
        // browser to have a particular page or item in your iTunes U
        // site displayed to that user in iTunes. Replace "/Browse/" in
        // the code below with "/API/GetBrowseURL/" if you instead want
        // to return the URL that would need to be opened to have that
        // page or item displayed in iTunes.
        String prefix = siteURL.substring(0, siteURL.indexOf(".woa/") + 4);
        String url = prefix + "/Browse/" + destination;
        String results = iTunesU.invokeAction(url, token);
        System.out.println(results);
        
    }

}

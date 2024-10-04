package util;

import java.io.*;
import java.net.*;

public class HTTPRequest {

    public String method;
    public String contentType;
    public int contentLength;

    public String body; 

    public void ParseRequestString(String request, BufferedReader in) throws IOException {

        System.out.println("Received request:\n" + request);

        // Split up sections...

        String[] requestLines = request.split("\n");
        String requestLine = requestLines[0];
        String[] requestParts = requestLine.split(" ");
        this.method = requestParts[0];
        //String path = requestParts[1];

        // Read Content Length
        this.contentLength = 0;
        for (String headerLine : requestLines) {
            if (headerLine.startsWith("Content-Length: ")) {
                this.contentLength = Integer.parseInt(headerLine.substring(16).trim());
            }
        }

        // Read body
        char[] bodyChars = new char[this.contentLength];
        
        in.read(bodyChars, 0, this.contentLength);
        this.body = new String(bodyChars);

        System.out.println("BODY: " + this.body);

        return;
    }
}
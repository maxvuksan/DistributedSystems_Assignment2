import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// compile & run
// javac -cp "lib/json-simple-1.1.1.jar"  GETClient.java
// java -cp "lib/json-simple-1.1.1.jar;." GETClient 


public class GETClient {

    public static void FetchData(String serverUrl, String contentServerId){

        String serverAddress;
        int serverPort;

        // interpret URL
        try{
            URI uri = new URI(serverUrl);
            serverAddress = uri.getHost();
            serverPort = uri.getPort() != -1 ? uri.getPort() : (uri.getScheme().equals("https") ? 443 : 80);
        }
        catch (URISyntaxException e) {
            System.err.println("Invalid server URL: " + e.getMessage());
            return;
        }

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) 
        {

            if(contentServerId == ""){
                contentServerId = "none";
            }


            String body = "{ \"id\": \"" + contentServerId + "\"}";

            String httpRequest = """
                    GET /weather.json HTTP/1.1
                    User-Agent: ATOMClient/1/0
                    Content-Type: application/json
                    Content-Length: %d
                    
                    %s
                    """;

            httpRequest = String.format(httpRequest, body.length(), body);

            // Fetching data
            out.println(httpRequest); // Sending a request to fetch data

            StringBuilder requestBuilder = new StringBuilder();
            String line;
            
            // Read the request line by line
            while (true) {

                line = in.readLine();

                if(line == null || line.isEmpty()){
                    break;
                }
                requestBuilder.append(line).append("\n");
            }
              
            if(requestBuilder.toString() == null || requestBuilder.toString().isEmpty()){
                return;
            }

            JSONParser parser = new JSONParser();
            JSONObject message = null;
            try {
                message = (JSONObject) parser.parse(requestBuilder.toString());
            } 
            catch (ParseException e) {

                System.out.println("Failed to parse JSON: " + e.getMessage());
                return;
            }

            HandleResponse(message);
        
        } 
        catch(ConnectException e){
            System.out.println("Failed to connect to server: " + e.getMessage());
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void HandleResponse(JSONObject jsonObject){
        
        // print the weather data in a nice format
        
        System.out.println(jsonObject.toJSONString());

        StringBuilder stringBuilder = new StringBuilder();

        for (Object key : jsonObject.keySet()) {
            String value = jsonObject.get(key).toString();

            // ignore non weather data
            if ("lamport_time".equals(key) || "status".equals(key)) {
                continue;
            }

            stringBuilder.append(key).append(":").append(value).append("\n");
        }
        
        System.out.println("Status: " + jsonObject.get("status"));
        System.out.println(stringBuilder.toString().trim()); // Remove the trailing newline
    }


}

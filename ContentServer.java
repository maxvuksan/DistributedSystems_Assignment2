import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import util.LamportClock;
import util.LamportClock;

//import lamport.LamportClock;



public class ContentServer {

    private LamportClock lamportClock = new LamportClock();

    @SuppressWarnings("unchecked")
    public JSONObject ParseWeatherFile(String filePath) {

        JSONObject jsonObject = new JSONObject();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            String line;
            while ((line = br.readLine()) != null) {

                // split by key & pair
                String[] parts = line.split(":", 2); // Limit to 2 parts
                if (parts.length == 2) {

                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    jsonObject.put(key, value); // Add to JSON object
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }

    @SuppressWarnings("unchecked")
    public void PostData(String serverUrl, String filePath){

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

        JSONObject jsonData = ParseWeatherFile(filePath);
        jsonData.put("lamport_time", 0);

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) 
        {

            String requestFormat = "{ \"method\": \"POST\", \"data\": %s }";

            String httpRequest = """
                    PUT /weather.json HTTP/1.1
                    User-Agent: ATOMClient/1/0
                    Content-Type: application/json
                    Content-Length: %d
                    
                    %s
                    """;

            String weatherDataRaw = jsonData.toJSONString();
            httpRequest = String.format(httpRequest, weatherDataRaw.length(), weatherDataRaw);

            System.out.println("Submitted content from: " + filePath);

            lamportClock.increment();

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
        catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void HandleResponse(JSONObject jsonData){

        System.out.println("Status: " + jsonData.get("status"));

        Object lamportTime = jsonData.get("lamport_time");

        // update lamport clock
        lamportClock.update(((Number) lamportTime).intValue());
    }

}

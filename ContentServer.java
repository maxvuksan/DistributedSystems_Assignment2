import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import lamport.LamportClockImpl;

// compile & run
// javac -cp "lib/json-simple-1.1.1.jar" -Xlint:unchecked ContentServer.java
// java -cp "lib/json-simple-1.1.1.jar;." ContentServer 

public class ContentServer {

    //private static LamportClock lamportClock = new LamportClockImpl();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("[URL      ]: ");
         
        String url = scanner.nextLine(); 

        if(url == ""){ // default address
            url = "http://127.0.0.1:9000";
        }

        System.out.print("[FILE PATH]: ");
        String filePath = scanner.nextLine(); 

        while(true){

            System.out.println("\n[Press Enter to POST Weather Data, Type 'exit' To Close]");
            String input = scanner.nextLine(); // waits until Enter is pressed

            if("exit".equalsIgnoreCase(input)){
                break;
            }

            PostData(url, filePath);
        }

        scanner.close();

    }

    @SuppressWarnings("unchecked")
    public static JSONObject ParseWeatherFile(String filePath) {

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
    public static void PostData(String serverUrl, String filePath){

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



    public static void HandleResponse(JSONObject jsonObject){

        System.out.println("Status: " + jsonObject.get("status"));

        // update lamport clock with new value
        
        //System.out.println(response.toString());
        // recieves {lamport_time: x}

        // lamportClock.update(...recievedTime);


    }

}

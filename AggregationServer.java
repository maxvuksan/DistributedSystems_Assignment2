import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import util.*;


    // compile & run 
    // javac -cp "lib/json-simple-1.1.1.jar" util/HTTPRequest.java util/WeatherData.java AggregationServer.java
    // java -cp "lib/json-simple-1.1.1.jar;." AggregationServer


    public class AggregationServer {

        private static final int PORT = 9000;
        //private static final ConcurrentHashMap<String, String> dataStore = new ConcurrentHashMap<>();
        //private static JSONNode latestWeatherData = new JSONNode();
        //private static JSONObject weatherDataMap = new JSONObject();

        private static Map<String, WeatherData> weatherDataMap = new HashMap<>();
        //private TreeMap<LocalDateTime, String> dateMap = new TreeMap<>(); // Keep track of dates
        
        public static void main(String[] args) {

            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                
                System.out.println("Server listening on port " + PORT);
                
                // accept a clients connection (this could be GETClient or ContentServer)
                while (true) {
                    new ClientHandler(serverSocket.accept()).start();
                }
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static class ClientHandler extends Thread {
            private Socket socket;

            public ClientHandler(Socket socket) {
                this.socket = socket;
            }

            public void run() {

                try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) 
                {
                    
                    // when a request comes in we determine if other requests need to be removed
                    ExpireWeatherDataIfNecassary();

                    // Collect input into single string -----------------------------------------

                    StringBuilder requestBuilder = new StringBuilder();
                    String line;

                    // Read the request line by line
                    while (!(line = in.readLine()).isEmpty()) {
                        requestBuilder.append(line).append("\n");
                    }

                    // Parse HTTP Request ------------------------------------------------------
                    HTTPRequest httpRequest = new HTTPRequest();
                    httpRequest.ParseRequestString(requestBuilder.toString(), in);

                    
                    JSONObject jsonBody = ParseHTTPBodyToJson(httpRequest.body);

                    if(jsonBody == null){
                        out.println("{\"status\": \"500\"}");
                        return;
                    }

                    if("PUT".equals(httpRequest.method)){
        
                        out.println(HandleContentServer(jsonBody));
                        return;
                    }
                    else if("GET".equals(httpRequest.method)){

                        out.println(HandleGETClient(jsonBody));
                        return;
                    }

                    // invalid request
                    out.println("{\"status\": \"400\"}"); 
                } 
                catch (IOException e) {

                    e.printStackTrace();
                }
            }


            // weather data expires after 30 seconds
            private void ExpireWeatherDataIfNecassary(){

                boolean noneExpired = false;
                while(!noneExpired){
                    noneExpired = true;

                    for (Object key : weatherDataMap.keySet()) {

                        boolean isThirtySecondsApart = Duration.between(weatherDataMap.get(key).date, LocalDateTime.now()).getSeconds() >= 30;

                        if(isThirtySecondsApart){

                            weatherDataMap.remove(key);
                            noneExpired = false;
                            break;
                        }
                    }
                }

            }


            private JSONObject ParseHTTPBodyToJson(String body){

                JSONParser parser = new JSONParser();

                JSONObject message = null;
                try {
                    message = (JSONObject) parser.parse(body);
                } 
                catch (ParseException e) {

                    System.out.println("Failed to parse JSON: " + e.getMessage());
                    return null;
                }
                return message;

            }

            private String HandleContentServer(JSONObject jsonData){

                System.out.println("PUT from Content Server");
        
                int statusCode = 200;

                String id = (String)jsonData.get("id").toString();

                WeatherData newWeatherData = new WeatherData(jsonData, LocalDateTime.now());
        
                // Check if the id already exists
                if (weatherDataMap.containsKey(id)) {
                    
                    WeatherData existingData = weatherDataMap.get(id);
                    // Only update if the new data is more recent
                    //(COMPARE LAMPORT CLOCK??? ONLY UPDATE IF LAMPORT CLOCK IS NEWER if (newWeatherData.getDate().isAfter(existingData.getDate())) {
                    weatherDataMap.put(id, newWeatherData);
                    //}
                    statusCode = 200; // Status = OK
                } 
                else {
                    // Add new entry if it doesn't exist
                    weatherDataMap.put(id, newWeatherData);
                    statusCode = 201; // Status = HTTP_CREATED
                }

                ProcessContentServerMap();

                String response = "{\"lamport_time\": \"%d\", \"status\": \"%d\"}";
                response = String.format(response, 0, statusCode);

                return response;
            }

            // ensures the map is correctly ordered, and only a max of 20 elements, 
            void ProcessContentServerMap(){

                // remove oldest (keep only 20)
            }




            @SuppressWarnings("unchecked")
            private String HandleGETClient(JSONObject jsonData){

                System.out.println("GET from Client");

                String id = jsonData.get("id").toString();
                
                JSONObject responseObj = new JSONObject();
                responseObj.put("status", 400);
                
                JSONObject copyTarget; 
                if (weatherDataMap.containsKey(id)) {
                    copyTarget = weatherDataMap.get(id).json;
                } 
                else{
                    return responseObj.toJSONString();
                }
                
                // copy to response JSON object
                for (Object key : copyTarget.keySet()) {
                    responseObj.put(key, copyTarget.get(key)); // Add each entry to the new JSONObject
                }

                responseObj.put("status", 200);
                responseObj.put("lamport_time", 0);

                return responseObj.toJSONString();
            }

        }
    
    }

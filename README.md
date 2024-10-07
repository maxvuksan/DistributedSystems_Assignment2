# Distributed Systems - Assignment 2

### Aggregation Server
javac -cp "lib/json-simple-1.1.1.jar" util/*.java AggregationServer.java
java -cp "lib/json-simple-1.1.1.jar;." AggregationServer

runs on port 9000, can generally be connected to with http://127.0.0.1:9000

### GETClient (utalizing GETClientMain interaface)
javac -cp "lib/json-simple-1.1.1.jar" util/*.java  GETClient.java
javac -cp "lib/json-simple-1.1.1.jar" util/*java GETClient.java GETClientMain.java
java -cp "lib/json-simple-1.1.1.jar;." GETClientMain 

### ContentServer (utalizing ContentServerMain interaface)
javac -cp "lib/json-simple-1.1.1.jar" util/*.java  ContentServer.java
javac -cp "lib/json-simple-1.1.1.jar" util/*java ContentServer.java ContentServerMain.java
java -cp "lib/json-simple-1.1.1.jar;." ContentServerMain 
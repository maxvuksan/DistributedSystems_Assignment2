import java.io.*;
import java.net.*;
import java.util.Scanner;


// compile & run
// javac -cp "lib/json-simple-1.1.1.jar" -Xlint:unchecked util/LamportClock.java ContentServer.java ContentServerMain.java
// java -cp "lib/json-simple-1.1.1.jar;." ContentServerMain

public class ContentServerMain {
    
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("[URL      ]: ");
         
        String url = scanner.nextLine(); 

        if(url == ""){ // default address
            url = "http://127.0.0.1:9000";
        }

        System.out.print("[FILE PATH]: ");
        String filePath = scanner.nextLine(); 


        ContentServer contentServer = new ContentServer();

        while(true){

            System.out.println("\n[Press Enter to POST Weather Data, Type 'exit' To Close]");
            String input = scanner.nextLine(); // waits until Enter is pressed

            if("exit".equalsIgnoreCase(input)){
                break;
            }

            contentServer.PostData(url, filePath);
        }

        scanner.close();
    }


}

import java.util.Scanner;

public class GETClientMain {

        public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("[URL]: ");
        String url = scanner.nextLine(); 

        if(url == ""){ // default address
            url = "http://127.0.0.1:9000";
        }

        System.out.print("[ID ]: ");
        String id = scanner.nextLine(); 
                
        GETClient getClient = new GETClient();

        while(true){

            System.out.println("\n[Press Enter to Fetch Weather Data, Type 'exit' To Close]");
            String input = scanner.nextLine(); // waits until Enter is pressed

            if("exit".equalsIgnoreCase(input)){
                break;
            }

            getClient.FetchData(url, id);
        }

        scanner.close();

    }

}

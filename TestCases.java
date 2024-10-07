
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * testing the interactions between the client and the server (run server first)
 * 
 */
public class TestCases {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        
        AggregationServer.Start();
    }

    @After
    public void tearDown() {
        System.setOut(standardOut);
    }

    @SuppressWarnings("static-access")
    @Test
    public void contentPUT() {
        
        try {

            ContentServer serv1 = new ContentServer();
            serv1.PostData("http://127.0.0.1:9000", "weather.txt");

            GETClient client = new GETClient();
            client.FetchData("http://127.0.0.1:9000", "IDS60901");

            ContentServer serv2 = new ContentServer();
            serv1.PostData("http://127.0.0.1:9000", "weather.txt");

            // status code for first time update 201
            //Assert.assertEquals("Status: 201", outputStreamCaptor.toString().trim());

        } catch (Exception e) {
            System.err.println("Test exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void clientGET() {
        
        try {

            ContentServer serv1 = new ContentServer();
            serv1.PostData("http://127.0.0.1:9000", "weather.txt");

            GETClient client = new GETClient();
            client.FetchData("http://127.0.0.1:9000", "IDS60901");


        } catch (Exception e) {
            System.err.println("Test exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void contentExpire() {
        
        try {

            ContentServer serv1 = new ContentServer();
            serv1.PostData("http://127.0.0.1:9000", "weather.txt");

            Thread.sleep(32000);

            GETClient client = new GETClient();
            client.FetchData("http://127.0.0.1:9000", "IDS60901");


        } catch (Exception e) {
            System.err.println("Test exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("static-access")
    @Test
    public void doubleContentPUT() {
        
        try {

            ContentServer serv1 = new ContentServer();
            serv1.PostData("http://127.0.0.1:9000", "weather.txt");
            Thread.sleep(1000);
            serv1.PostData("http://127.0.0.1:9000", "weather2.txt");


        } catch (Exception e) {
            System.err.println("Test exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
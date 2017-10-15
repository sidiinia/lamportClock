import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client2 {
    private static int clockTime = 0;
    private static int procId = 2;
    private static int numOfLikes = 0;

    public static void main(String[] args) throws Exception {

        Connection c1 = new Connection("127.0.0.1", 3000, procId, clockTime);
        Connection c2 = new Connection("127.0.0.1", 4000, procId, clockTime);
        c1.start();
        c2.start();

        // test content
        System.out.println("TEST CONTENT:");

        String clientMessage = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (!clientMessage.equals("quit")) {

                clientMessage = br.readLine();
                if (!clientMessage.equals("")) {
                    System.out.println("user input is " + clientMessage);
                    if (clientMessage.equals("like")) {
                        increaseClockTime(-1);
                        Packet packet = new Packet("Sent from Client 2", procId, clockTime);
                        c1.write(packet);
                        c2.write(packet);
                        //sendPacket();
                    }
                    //c1.write(clientMessage);
                    //c2.write(clientMessage);
                }
            }
        } catch (IOException e) {

        }
    }

    public static void increaseClockTime(int piggybackTime) {
        clockTime = clockTime > piggybackTime ? clockTime : piggybackTime;
        clockTime++;
        System.out.println("Current clock value for Client 2 is " + clockTime);
    }
    public static void setClockTime(int clockTime) {
        Client2.clockTime = clockTime;
        System.out.println("Current clock value for Client 2 is " + clockTime);
    }
}
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client2 {
    public static int clockTime = 0;
    private static int procId = 2;
    public static int numOfLikes = 0;

    public static void main(String[] args) throws Exception {

        Connection c1 = new Connection("127.0.0.1", 3000, procId, clockTime);
        Connection c2 = new Connection("127.0.0.1", 4000, procId, clockTime);
        c1.start();
        c2.start();

        String clientMessage = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (!clientMessage.equals("quit")) {

                clientMessage = br.readLine();
                if (!clientMessage.equals("")) {
                    System.out.println("user input is " + clientMessage);
                    if (clientMessage.equals("like")) {
                        //Thread.sleep(5000);
                        clockTime++;
                        System.out.println("Current clock value for Client 2 is (" + clockTime + ", " + procId + ")");
                        //System.out.println("CLIENT 2: "+ clockTime);
                        numOfLikes++;
                        System.out.println("TESTCASE CONTENT     Like: " + numOfLikes);
                        Packet packet = new Packet("Sent from Client 2", procId, clockTime, numOfLikes);
                        c1.write(packet);
                        c2.write(packet);
                    }
                }
            }
        } catch (IOException e) {

        }
    }

    public static void increaseClockTime(Packet packet) {
        clockTime = clockTime > packet.getTime() ? clockTime : packet.getTime();
        clockTime++;
        //System.out.println("Current clock value for Client 2 is (" + clockTime + ", " + procId + ")");
    }

    public static int increaseLikes(Packet p) {
        numOfLikes++;
        return numOfLikes;
    }
}
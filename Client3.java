import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client3 {
    public static int clockTime = 0;
    private static int procId = 3;
    public static int numOfLikes = 0;

    public static void main(String[] args) throws Exception {

        Connection c1 = new Connection("127.0.0.1", 2000, procId, clockTime);
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
                        System.out.println("Current clock value for Client 3 is (" + clockTime + ", " + procId + ")");
                        //System.out.println("CLIENT 3: "+ clockTime);
                        numOfLikes++;
                        System.out.println("TESTCASE CONTENT     Like: " + numOfLikes);
                        Packet packet = new Packet("Sent from Client 3", procId, clockTime, numOfLikes);
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
        //System.out.println("Current clock value for Client 3 is (" + clockTime + ", " + procId + ")");
    }

    public static int increaseLikes(Packet p) {
        numOfLikes++;
        return numOfLikes;
    }
}
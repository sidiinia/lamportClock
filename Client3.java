import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client3 {
    private static int clockTime = 0;
    private static int procId = 3;
    private static int numOfLikes = 0;

    public static void main(String[] args) throws Exception {
        Connection c1 = new Connection("127.0.0.1", 2000);
        Connection c2 = new Connection("127.0.0.1", 2200);

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
                        Packet packet = new Packet("Sent from Client3", 3, clockTime);
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
        System.out.println("Current clock value for Client 3 is " + clockTime);
    }
}
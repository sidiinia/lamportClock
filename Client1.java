import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client1 {
    private static int clockTime = 0;
    private static int procId = 1;
    private static int numOfLikes = 0;

    public static void main(String[] args) throws Exception {

        Connection c1 = new Connection("127.0.0.1", 2000, procId, clockTime);
        Connection c2 = new Connection("127.0.0.1", 3000, procId, clockTime);
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
                        Thread.sleep(5000);
                        increaseClockTime(-1);
                        numOfLikes++;
                        System.out.println("Client 1 has " + numOfLikes + " likes");
                        Packet packet = new Packet("Sent from Client 1", procId, clockTime, numOfLikes);
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
        System.out.println("Current clock value for Client 1 is " + clockTime);
    }

    public static void increaseLikes(Packet p) {
        if (p.getTime() < clockTime) {
            numOfLikes = p.getNumOfLikes();
        } else if (p.getTime() == clockTime && p.getProcessId() < procId){
            numOfLikes = p.getNumOfLikes();
        } else {
            numOfLikes++;
        }
        System.out.println("Client 1 has " + numOfLikes + " likes");
    }

    public static void setClockTime(int clockTime) {
        Client1.clockTime = clockTime;
        System.out.println("Current clock value for Client 1 is " + clockTime);
    }
}
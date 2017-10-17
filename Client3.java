import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Client3 {
    public static int clockTime = 0;
    public static int procId = 3;
    public static int numOfLikes = 0;
    public static int replyCounter = 0;
    public static PriorityQueue<Packet> q3 = new PriorityQueue<>(10, new Comparator<Packet>() {

        @Override
        public int compare(Packet o1, Packet o2) {
            if (o1.getTime() < o2.getTime()
                    || (o1.getTime() == o2.getTime() && o1.getProcessId() < o2.getProcessId())) {
                return -1;
            } else {
                return 1;
            }
        }
    });

    static int REQUEST = 1;
    static int REPLY = 2;
    static int RELEASE = 3;

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
                        Packet packet = new Packet(REQUEST, "this is a request packet from client 3", procId, clockTime, numOfLikes);
                        q3.add(packet);
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
        System.out.println("Lamport Clock for Client 3 is (" + clockTime + ", 3)");
        //System.out.println("Current clock value for Client 3 is (" + clockTime + ", " + procId + ")");
    }

    public static int increaseLikes() {
        numOfLikes++;
        System.out.println("TESTCASE CONTENT. LIKE: " + numOfLikes);
        return numOfLikes;
    }
}
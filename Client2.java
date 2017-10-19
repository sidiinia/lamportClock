import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Client2 {
    public static int clockTime = 0;
    public static int procId = 2;
    public static volatile int numOfLikes = 0;
    public static volatile int replyCounter = 0;
    public static volatile boolean critical = false;

    //public static boolean ready = false;

    public static PriorityQueue<Packet> q2 = new PriorityQueue<>(10, new Comparator<Packet>() {
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
    public static Packet packet;

    public static void main(String[] args) throws Exception {

        Connection c1 = new Connection("127.0.0.1", 3000, procId);
        Connection c2 = new Connection("127.0.0.1", 4000, procId);
        c1.start();
        c2.start();

        String clientMessage = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (!clientMessage.equals("quit")) {

                clientMessage = br.readLine();
                if (!clientMessage.equals("")) {
                    if (clientMessage.equals("like")) {
                        //Thread.sleep(5000);
                        clockTime++;
                        System.out.println("Current clock value for Client 2 is (" + clockTime + ", " + procId + ")");
                        packet = new Packet(REQUEST, "Request packet from client 2", procId, clockTime, numOfLikes);
                        c1.write(packet);
                        clockTime++;
                        System.out.println("Current clock value for Client 2 is (" + clockTime + ", " + procId + ")");
                        packet = new Packet(REQUEST, "Request packet from client 2", procId, clockTime, numOfLikes);
                        q2.add(packet);
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
        System.out.println("Lamport Clock for Client 2 is (" + clockTime + ", 2)");
    }

    public static int increaseLikes() {
        numOfLikes++;
        System.out.println("TESTCASE CONTENT. LIKE: " + numOfLikes);
        return numOfLikes;
    }
}
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client2 {
    public static int clockTime = 0;
    public static int procId = 2;
    public static int numOfLikes = 0;
    public static int replyCounter = 0;
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
    static int REPLY = 2;
    static int RELEASE = 3;

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
                    if (clientMessage.equals("like")) {
                        //Thread.sleep(5000);
                        clockTime++;
                        System.out.println("Current clock value for Client 2 is (" + clockTime + ", " + procId + ")");
                        //System.out.println("CLIENT 2: "+ clockTime);
                        //numOfLikes++;
                        //System.out.println("TESTCASE CONTENT     Like: " + numOfLikes);
                        Packet packet = new Packet(REQUEST, "Request packet from client 2", procId, clockTime, numOfLikes);
                        q2.add(packet);
                        //ready = false;
                        c1.write(packet);
                        c2.write(packet);

                        // when replyCounter is 3, client is ready to release
                        while (replyCounter != 3) {}
                        c1.sendReleasePacket(Client1.clockTime, procId);
                        c2.sendReleasePacket(Client3.clockTime, procId);
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
        //System.out.println("Current clock value for Client 2 is (" + clockTime + ", " + procId + ")");
    }

    public static int increaseLikes() {
        numOfLikes++;
        System.out.println("TESTCASE CONTENT. LIKE: " + numOfLikes);
        return numOfLikes;
    }
}
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Connection implements Runnable, Serializable {

    private String host;
    private int port;
    private static int clientClock;
    //private DataOutputStream outStream;
    //private DataInputStream inStream;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private boolean running;
    private static int clientId;
    private volatile Socket returnSocket;
    Packet packet = null;

    public Connection(String host, int port, int clientId, int clientClock) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.clientClock = clientClock;
    }

    public void start() {
        try {
            System.out.println("initialize connection for port " + port);
            returnSocket = new Socket(host, port);
            //outStream = new DataOutputStream(returnSocket.getOutputStream());
            //inStream = new DataInputStream(returnSocket.getInputStream());
        } catch (IOException e) {

        }
        running = true;

        new Thread(this).start();
    }

    // read from servers
    @Override
    public void run() {
        try {
            outStream = new ObjectOutputStream(returnSocket.getOutputStream());
            //inStream = new ObjectInputStream(returnSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = true;

        while (running) {
            try {
                packet = this.read(returnSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (packet.getMessage() == null || packet.getMessage().equals("quit")) {
                break;
            }
            if (packet.getMessage() != null && (!packet.getMessage().equals(""))) {
                System.out.println("received packet:" + packet.getMessage());
                //int senderClockTime = packet.getTime();
                //clientClock =  clientClock > senderClockTime? clientClock : senderClockTime;
                //clientClock++;
                switch (packet.getProcessId()) {
                    case (1) :
                        Client2.increaseLikes(packet);
                        Client2.increaseClockTime(packet);
                        if (clientId == 2) {
                            System.out.println("TESTCASE CONTENT     Like: " + Client2.numOfLikes);
                            System.out.println("Current clock value for Client " + clientId + " is (" + Client2.clockTime + ", " + clientId + ")");
                        }
                        Client3.increaseLikes(packet);
                        Client3.increaseClockTime(packet);
                        if (clientId == 3) {
                            System.out.println("TESTCASE CONTENT     Like: " + Client3.numOfLikes);
                            System.out.println("Current clock value for Client " + clientId + " is (" + Client3.clockTime + ", " + clientId + ")");
                        }
                        break;
                    case (2) :
                        Client1.increaseLikes(packet);
                        Client1.increaseClockTime(packet);
                        if (clientId == 1) {
                            System.out.println("TESTCASE CONTENT     Like: " + Client1.numOfLikes);
                            System.out.println("Current clock value for Client " + clientId + " is (" + Client1.clockTime + ", " + clientId + ")");
                        }
                        Client3.increaseLikes(packet);
                        Client3.increaseClockTime(packet);
                        if (clientId == 3) {
                            System.out.println("TESTCASE CONTENT     Like: " + Client3.numOfLikes);
                            System.out.println("Current clock value for Client " + clientId + " is (" + Client3.clockTime + ", " + clientId + ")");
                        }
                        break;
                    case (3) :
                        Client1.increaseLikes(packet);
                        Client1.increaseClockTime(packet);
                        if (clientId == 1) {
                            System.out.println("TESTCASE CONTENT     Like: " + Client1.numOfLikes);
                            System.out.println("Current clock value for Client " + clientId + " is (" + Client1.clockTime + ", " + clientId + ")");
                        }
                        Client2.increaseLikes(packet);
                        Client2.increaseClockTime(packet);
                        if (clientId == 2) {
                            System.out.println("TESTCASE CONTENT     Like: " + Client2.numOfLikes);
                            System.out.println("Current clock value for Client " + clientId + " is (" + Client2.clockTime + ", " + clientId + ")");
                        }
                        break;
                    default:
                        break;
                }
                packet = null;
            }
        }
        System.out.println("Connection out of while loop");
        try {
            //inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read from servers

    public Packet read(Socket returnSocket) throws IOException {

        ObjectInputStream inStream = new ObjectInputStream(returnSocket.getInputStream());
        Packet packet = null;
        //System.out.println("in function read");
        try {
            packet = (Packet)inStream.readObject();
        } catch (EOFException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //inStream.close();
        //System.out.println("finish reading");
        return packet;
    }

    // client writes to the severs it's connected to.
    public void write(Packet packet) throws IOException {
        outStream.writeObject(packet);
        outStream.flush();
    }
}

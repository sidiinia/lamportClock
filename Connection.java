import java.io.*;
import java.net.Socket;

public class Connection implements Runnable, Serializable {

    private String host;
    private int port;
    //private DataOutputStream outStream;
    //private DataInputStream inStream;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private boolean running;
    Socket returnSocket;

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
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
            inStream = new ObjectInputStream(returnSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = true;

        while (running) {
            Packet packet = this.read();
            if (packet.getMessage() == null || packet.getMessage().equals("quit")) {
                break;
            }
            if (packet.getMessage() != null && (!packet.getMessage().equals(""))) {
                System.out.println("received message:" + packet.getMessage());
                
            }
        }
        try {
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read from servers
    public Packet read() {
        //System.out.println("in function read");
        Packet packet = null;
        try {
            packet = (Packet)inStream.readObject();
        } catch (EOFException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //System.out.println("finish reading");
        return packet;
    }

    // client writes to the severs it's connected to.
    public void write(Packet packet) throws IOException {
        outStream.writeObject(packet);
        outStream.flush();
    }
    public Socket getReturnSocket() {
        return returnSocket;
    }
}

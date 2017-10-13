import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Connection implements Runnable{

    private String host;
    private int port;
    private DataOutputStream outStream;
    private DataInputStream inStream;
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
            outStream = new DataOutputStream(returnSocket.getOutputStream());
            inStream = new DataInputStream(returnSocket.getInputStream());
        } catch (IOException e) {

        }
        running = true;

        new Thread(this).start();
    }

    @Override
    public void run() {

        running = true;

        while (running) {
            String s = this.read();
            if (s != null && (!s.equals(""))) {
                System.out.println("received messages :" + s);
            }
        }
        try {
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        //System.out.println("in function read");
        String s = null;
        try {
            s = inStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("finish reading");
        return s;
    }

    public void write(String message) throws IOException {
        outStream.writeUTF(message);
        outStream.flush();
    }
    public Socket getReturnSocket() {
        return returnSocket;
    }
}

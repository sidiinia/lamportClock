import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.Socket;

public class Connection implements Runnable, Serializable{

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

    static int REQUEST = 1;
    static int REPLY = 2;
    static int RELEASE = 3;

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

            if (packet.getMessage() != null && (!packet.getMessage().equals(""))) {
                switch (packet.getProcessId()) {
                    case (1) :
                        System.out.println("in case 1 -- sender is 1");
                        if (clientId == 2)  {
                            client2Handler(packet,Client1.clockTime);
                        }

                        if (clientId == 3)  {
                            client3Handler(packet, Client1.clockTime);
                        }
                        break;

                    case (2) :
                        System.out.println("in case 2 -- sender is 2");
                        if (clientId == 1)  {
                            client1Handler(packet, Client2.clockTime);
                        }

                        if (clientId == 3)  {
                            client3Handler(packet, Client2.clockTime);
                        }
                        break;

                    case (3) :
                        System.out.println("in case 3 -- sender is 3");
                        if (clientId == 1)  {
                            client1Handler(packet, Client3.clockTime);
                        }

                        if (clientId == 2)  {
                            client2Handler(packet, Client3.clockTime);
                        }
                        break;
                    default:
                        System.out.println("Out of implemented clients scope");
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

    public void sendReplyPacket(int clockTime, int procId) throws IOException {
        System.out.println("Sending reply packet from client " + procId + "...");
        Packet p = new Packet(REPLY, "Reply packet from client " + procId, procId, clockTime+1, 0); //todo
        outStream.writeObject(p);
        outStream.flush();
    }

    public void sendReleasePacket(int clockTime, int procId) throws IOException {
        System.out.println("Sending release packet from client " + procId + "...");
        Packet p = new Packet(RELEASE, "Release packet from client " + procId, procId, clockTime+1, 0); //todo
        outStream.writeObject(p);
        outStream.flush();
    }

    public synchronized void sleep(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void client1Handler(Packet packet, int clockTime) {
        System.out.println("Client 1 received packet: " + packet.getMessage());
        // receive request packet
        if (packet.getType() == REQUEST) {
            Client1.q1.add(packet);

            // increase clock time
            Client1.increaseClockTime(packet);

            while (Client1.critical) {}

            // construct and send reply packet
            try {
                sendReplyPacket(clockTime, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // receive reply packet
        if (packet.getType() == REPLY) {
            Client1.increaseClockTime(packet);
            Client1.replyCounter = (Client1.replyCounter >= 2) ? 1 : Client1.replyCounter+1;
            System.out.println("Client 1 has " + Client1.replyCounter + " reply");
            if (Client1.q1.peek().getProcessId() == 1 && Client1.replyCounter == 2) {
                System.out.println("1 received all replies");
                Client1.q1.poll();
                // enter critical section
                Client1.critical = true;
                sleep(5000);
                Client1.critical = false;
                Client1.increaseLikes();
                Client1.replyCounter++;
            }
            // when replyCounter is 3, client is ready to release
            while (Client1.replyCounter != 3) {}
            try {
                sendReleasePacket(clockTime, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Client1.replyCounter = 0;
        }

        // receive release packet
        if (packet.getType() == RELEASE) {
            Client1.q1.poll();
            Client1.increaseLikes();
            Client1.increaseClockTime(packet);
            if (!Client1.q1.isEmpty() && Client1.q1.peek().getProcessId() == 1 && Client1.replyCounter == 2) {
                Client1.q1.poll();
                // enter critical section
                Client1.critical = true;
                sleep(5000);
                Client1.critical = false;
                Client1.increaseLikes();
            }
        }
    }

    public synchronized void client2Handler(Packet packet, int clockTime) {
        System.out.println("Client 2 received packet: " + packet.getMessage());
        // receive request packet
        if (packet.getType() == REQUEST) {
            Client2.q2.add(packet);

            // increase clock time
            Client2.increaseClockTime(packet);
            while (Client2.critical) {}
            // construct and send reply packet
            try {
                sendReplyPacket(clockTime, 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // receive reply packet
        if (packet.getType() == REPLY) {
            Client2.increaseClockTime(packet);
            Client2.replyCounter = (Client2.replyCounter >= 2) ? 1 : Client2.replyCounter+1;
            System.out.println("Client 2 has " + Client2.replyCounter + " reply");

            if (Client2.q2.peek().getProcessId() == 2 && Client2.replyCounter == 2) {
                System.out.println("2 received all replies");
                Client2.q2.poll();
                // enter critical section
                Client2.critical = true;
                sleep(5000);
                Client2.critical = false;
                Client2.increaseLikes();
                Client2.replyCounter++;
            }
            // when replyCounter is 3, client is ready to release
            while (Client2.replyCounter != 3) {}
            try {
                sendReleasePacket(clockTime, 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Client2.replyCounter = 0;
        }

        // receive release packet
        if (packet.getType() == RELEASE) {
            Client2.q2.poll();
            Client2.increaseLikes();
            Client2.increaseClockTime(packet);
            if (!Client2.q2.isEmpty() && Client2.q2.peek().getProcessId() == 2 && Client2.replyCounter == 2) {
                Client2.q2.poll();
                // enter critical section
                Client2.critical = true;
                sleep(5000);
                Client2.critical = false;
                Client2.increaseLikes();
            }
        }
    }

    public synchronized void client3Handler(Packet packet, int clockTime) {
        System.out.println("Client 3 received packet: " + packet.getMessage());
        // receive request packet
        if (packet.getType() == REQUEST) {
            Client3.q3.add(packet);

            // increase clock time
            Client3.increaseClockTime(packet);
            while (Client3.critical) {}
            // construct and send reply packet
            try {
                sendReplyPacket(clockTime, 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // receive reply packet
        if (packet.getType() == REPLY) {
            Client3.increaseClockTime(packet);
            Client3.replyCounter = (Client3.replyCounter >= 2) ? 1 : Client3.replyCounter+1;
            System.out.println("Client 3 has " + Client3.replyCounter + " reply");
            if (Client3.q3.peek().getProcessId() == 3 && Client3.replyCounter == 2) {
                System.out.println("3 received all replies");
                Client3.q3.poll();
                // enter critical section
                Client3.critical = true;
                sleep(5000);
                Client3.critical = false;
                Client3.increaseLikes();
                Client3.replyCounter++;
            }
            // when replyCounter is 3, client is ready to release
            while (Client3.replyCounter != 3) {}
            try {
                sendReleasePacket(clockTime, 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Client3.replyCounter = 0;
        }

        // receive release packet
        if (packet.getType() == RELEASE) {
            Client3.q3.poll();
            Client3.increaseLikes();
            Client3.increaseClockTime(packet);
            if (!Client3.q3.isEmpty() && Client3.q3.peek().getProcessId() == 3 && Client3.replyCounter == 2) {
                Client3.q3.poll();
                // enter critical section
                Client3.critical = true;
                sleep(5000);
                Client3.critical = false;
                Client3.increaseLikes();
            }
        }
    }
}

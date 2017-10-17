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

            if (packet.getMessage() == null || packet.getMessage().equals("quit")) {
                break;
            }
            if (packet.getMessage() != null && (!packet.getMessage().equals(""))) {
                System.out.println("received packet: " + packet.getMessage());
                //int senderClockTime = packet.getTime();
                //clientClock =  clientClock > senderClockTime? clientClock : senderClockTime;
                //clientClock++;
                switch (packet.getProcessId()) {
                    case (1) :
                        System.out.println("in case 1 -- sender is 1");
                        if (clientId == 2)  {
                            // receive request packet
                            if (packet.getType() == REQUEST) {
                                Client2.q2.add(packet);

                                // increase clock time
                                Client2.increaseClockTime(packet);

                                // construct and send reply packet
                                try {
                                    sendReplyPacket(Client1.clockTime, 2);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // receive reply packet
                            if (packet.getType() == REPLY) {
                                Client2.increaseClockTime(packet);
                                Client2.replyCounter++;
                                if (Client2.q2.peek().getProcessId() == 2 && Client2.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client2.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();

                                    // construct and send release packet
                                    try {
                                        sendReleasePacket(Client1.clockTime, 2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // receive release packet
                            if (packet.getType() == RELEASE) {
                                Client2.increaseLikes();

                                Client2.increaseClockTime(packet);
                                if (Client2.q2.peek().getProcessId() == 2 && Client2.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client2.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();
                                }
                            }
                        }

                        /////////////////////////////////////////////////////////////////

                        if (clientId == 3)  {
                            // receive request packet
                            if (packet.getType() == REQUEST) {
                                Client3.q3.add(packet);

                                // increase clock time
                                Client3.increaseClockTime(packet);

                                // construct and send reply packet
                                try {
                                    sendReplyPacket(Client1.clockTime, 3);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // receive reply packet
                            if (packet.getType() == REPLY) {
                                Client3.increaseClockTime(packet);
                                Client3.replyCounter++;
                                if (Client3.q3.peek().getProcessId() == 3 && Client3.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client3.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();

                                    // construct and send release packet
                                    try {
                                        sendReleasePacket(Client1.clockTime, 3);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // receive release packet
                            if (packet.getType() == RELEASE) {
                                Client3.increaseLikes();

                                Client3.increaseClockTime(packet);
                                if (Client3.q3.peek().getProcessId() == 3 && Client3.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client3.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();
                                }
                            }
                        }
                        break;

                    case (2) :
                        System.out.println("in case 2 -- sender is 2");
                        if (clientId == 1)  {
                            // receive request packet
                            if (packet.getType() == REQUEST) {
                                Client1.q1.add(packet);

                                // increase clock time
                                Client1.increaseClockTime(packet);

                                // construct and send reply packet
                                try {
                                    sendReplyPacket(Client2.clockTime, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // receive reply packet
                            if (packet.getType() == REPLY) {
                                System.out.println("client 1 received reply from client 2");
                                Client1.increaseClockTime(packet);
                                Client1.replyCounter++;
                                if (Client1.q1.peek().getProcessId() == 1 && Client1.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client1.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();

                                    // construct and send release packet
                                    try {
                                        sendReleasePacket(Client2.clockTime, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // receive release packet
                            if (packet.getType() == RELEASE) {
                                Client1.increaseLikes();

                                Client1.increaseClockTime(packet);
                                if (Client1.q1.peek().getProcessId() == 1 && Client1.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client1.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();
                                }
                            }
                        }

                        /////////////////////////////////////////////////////////////////

                        if (clientId == 3)  {
                            // receive request packet
                            if (packet.getType() == REQUEST) {
                                Client3.q3.add(packet);

                                // increase clock time
                                Client3.increaseClockTime(packet);

                                // construct and send reply packet
                                try {
                                    sendReplyPacket(Client2.clockTime, 3);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // receive reply packet
                            if (packet.getType() == REPLY) {
                                Client3.increaseClockTime(packet);
                                Client3.replyCounter++;
                                if (Client3.q3.peek().getProcessId() == 3 && Client3.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client3.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();

                                    // construct and send release packet
                                    try {
                                        sendReleasePacket(Client2.clockTime, 3);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // receive release packet
                            if (packet.getType() == RELEASE) {
                                Client3.increaseLikes();

                                Client3.increaseClockTime(packet);
                                if (Client3.q3.peek().getProcessId() == 3 && Client3.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client3.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();
                                }
                            }
                        }
                        break;

                    case (3) :
                        System.out.println("in case 3 -- sender is 3");
                        if (clientId == 1)  {
                            // receive request packet
                            if (packet.getType() == REQUEST) {
                                Client1.q1.add(packet);

                                // increase clock time
                                Client1.increaseClockTime(packet);

                                // construct and send reply packet
                                try {
                                    sendReplyPacket(Client3.clockTime, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // receive reply packet
                            if (packet.getType() == REPLY) {
                                Client1.increaseClockTime(packet);
                                Client1.replyCounter++;
                                if (Client1.q1.peek().getProcessId() == 1 && Client1.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client1.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();

                                    // construct and send release packet
                                    try {
                                        sendReleasePacket(Client3.clockTime, 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // receive release packet
                            if (packet.getType() == RELEASE) {
                                Client1.increaseLikes();

                                Client1.increaseClockTime(packet);
                                if (Client1.q1.peek().getProcessId() == 1 && Client1.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client1.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();
                                }
                            }
                        }

                        /////////////////////////////////////////////////////////////////

                        if (clientId == 2)  {
                            // receive request packet
                            if (packet.getType() == REQUEST) {
                                Client2.q2.add(packet);

                                // increase clock time
                                Client2.increaseClockTime(packet);

                                // construct and send reply packet
                                try {
                                    sendReplyPacket(Client3.clockTime, 2);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // receive reply packet
                            if (packet.getType() == REPLY) {
                                Client2.increaseClockTime(packet);
                                Client2.replyCounter++;
                                if (Client2.q2.peek().getProcessId() == 2 && Client2.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client2.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();

                                    // construct and send release packet
                                    try {
                                        sendReleasePacket(Client3.clockTime, 2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // receive release packet
                            if (packet.getType() == RELEASE) {
                                Client2.increaseLikes();

                                Client2.increaseClockTime(packet);
                                if (Client2.q2.peek().getProcessId() == 2 && Client2.replyCounter == 2) {
                                    // enter critical section
                                    sleep(5000);
                                    Client2.increaseLikes();
                                    Client2.q2.poll();
                                    Client1.q1.poll();
                                    Client3.q3.poll();
                                }
                            }
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

    public void sendReplyPacket(int clockTime, int procId) throws IOException {
        Packet p = new Packet(REPLY, "this is a reply packet", procId, clockTime+1, 0); //todo
        outStream.writeObject(p);
        outStream.flush();
    }

    public void sendReleasePacket(int clockTime, int procId) throws IOException {
        Packet p = new Packet(RELEASE, "this is a release packet", procId, clockTime+1, 0); //todo
        outStream.writeObject(p);
        outStream.flush();
    }

    public void sleep(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

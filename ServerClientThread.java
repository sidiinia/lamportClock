import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

class ServerClientThread extends Thread {
    Socket socket;
    List<Socket> clientList;
    boolean running = true;
    String mes;
    //int clientNo;
    ServerClientThread(Socket socket, List<Socket> clientList){
        this.socket = socket;
        this.clientList = clientList;
    }
    public void run(){
        try{
            while (running) {
                DataInputStream inStream = new DataInputStream(socket.getInputStream());
                mes = inStream.readUTF();
                System.out.println("server received mes: " + mes);

                for (int i = 0; i < clientList.size(); i++) {
                    if (!clientList.get(i).equals(socket)) {
                        DataOutputStream outStream = new DataOutputStream(clientList.get(i).getOutputStream());
                        //System.out.println("server trying to write to socket" + clientList.get(i));
                        outStream.writeUTF(mes);
                    }
                }
            }
            //inStream.close();
            //outStream.close();
            socket.close();
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        } finally{
            System.out.println("Client -" + " exit!! ");
        }
    }
}

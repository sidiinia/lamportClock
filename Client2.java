import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client2 {
    public static void main(String[] args) throws Exception {
        Connection c1 = new Connection("127.0.0.1", 2100);
        Connection c2 = new Connection("127.0.0.1", 2200);

        c1.start();
        c2.start();

        String clientMessage = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (!clientMessage.equals("quit")) {

                clientMessage = br.readLine();
                if (!clientMessage.equals("")) {
                    System.out.println("user input is " + clientMessage);
                    c1.write(clientMessage);
                    c2.write(clientMessage);
                }
            }
        } catch (IOException e) {

        }
    }
}
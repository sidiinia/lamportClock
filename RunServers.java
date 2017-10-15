import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RunServers {

    public static void main(String[] args) throws Exception {
        CreateServerSocket s1 = new CreateServerSocket(2000);
        CreateServerSocket s2 = new CreateServerSocket(3000);
        CreateServerSocket s3 = new CreateServerSocket(4000);
        s1.start();
        s2.start();
        s3.start();
    }
}
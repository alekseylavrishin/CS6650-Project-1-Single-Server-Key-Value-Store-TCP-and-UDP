import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public void TCPServer(String[] args) throws IOException {
        if (args.length != 2){
            System.out.println("Proper input format must be 'java Client.java <server_ip> <port>'");
            return;
        }
        String serverIP = null;
        int port = -1;

        try{
            serverIP = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("<server_ip> must be type String and <port> must be type int");
        }
        Socket s = null;

        try {
            // Connect to server
            s = new Socket(serverIP, port);

            // Input and Output streams for server communication
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            // Get user input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter text: ");
            String line = scanner.nextLine();

            // Send user input to server
            out.writeUTF(line);

            // Read and print message returned from server
            String data = in.readUTF();
            System.out.print("Response from the server: " + data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        finally {
            if(s != null && !s.isClosed()) {
                s.close();
            }
        }

    }

    public static void main(String[] args) {

    }
}

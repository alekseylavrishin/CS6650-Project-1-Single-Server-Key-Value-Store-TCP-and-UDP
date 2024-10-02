import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

    /**
     * Performs communication with the server over TCP.
     * @param serverIP The IP Address or hostname of the server.
     * @param port The port number the server is listening on.
     * @throws Exception Throws exception if the server connection is interrupted or unsuccessful.
     */
    public static void TCPServer(String serverIP, int port) throws Exception {
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

    /**
     * Performs communication with the server over UDP.
     * @param serverIP The IP Address or hostname of the server.
     * @param port The port number the server is listening on.
     */
    public static void UDPClient(String serverIP, int port){
        DatagramSocket s = null;
        String testString = "Test string";
        try {
            s = new DatagramSocket();

            // Encode string into bytes for transmission
            byte[] byteStr = testString.getBytes();

            // Determines IP Address of the server given a hostname or IP
            InetAddress host = InetAddress.getByName(serverIP);
            DatagramPacket request = new DatagramPacket(byteStr, byteStr.length, host, port);
            s.send(request);

            byte[] buffer = new byte[1000]; // Buffer used to hold incoming datagram
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length); // Receive response

            // Test connection to server by sending message and receiving a reply
            s.receive(reply);
            System.out.println("Reply: " + new String(reply.getData()));

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(s != null) {
                s.close();
            }
        }
    }

    /**
     * Asks the user for the communication type they wish to use with the server.
     * User must enter '1' for TCP or '2' for UDP.
     * If provided input is not '1' or '2', function will rerun until appropriate input is given.
     * @param scanner The Scanner used for taking user input from System.in.
     */
    public static void askForCommType(Scanner scanner) {
        System.out.println("Enter '1' to use TCP or enter '2' to use UDP");
        int selection = scanner.nextInt();

        if(selection == 1) {
            //TCPServer(serverIP, port);
            System.out.println("TCP");
        } else if(selection == 2) {
            //UDPServer(serverIp, port);
            System.out.println("UDP");
        } else { // Rerun if input doesn't match '1' or '2'
            System.out.println("Invalid Input");
            askForCommType(scanner);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2){ // Check that 2 args are provided
            System.out.println("Proper input format must be 'java Client.java <server_ip> <port>'");
            return;
        }

        String serverIP = null;
        int port = -1;

        // Ensure provided args are correct types
        try{
            serverIP = args[0];
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("<server_ip> must be type String and <port> must be type int");
        }

        // Create scanner for accepting user input
        Scanner scanner = new Scanner(System.in);
        askForCommType(scanner);

    }
}
